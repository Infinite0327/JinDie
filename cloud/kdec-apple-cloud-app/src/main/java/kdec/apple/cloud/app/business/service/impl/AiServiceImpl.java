package kdec.apple.cloud.app.business.service.impl;

import com.alibaba.fastjson.JSON;
import kdec.apple.cloud.app.business.mapper.MaterialParseResultMapper;
import kdec.apple.cloud.app.business.mapper.TaskMapper;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.common.dto.answer.AnswerItem;
import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseClassReportVO;
import kdec.apple.cloud.app.common.dto.utils.RedisUtil;
import kdec.apple.cloud.app.entity.ExerciseItem;
import kdec.apple.cloud.app.entity.MaterialParseResult;
import kdec.apple.cloud.app.entity.Task;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";

    private final TaskMapper taskMapper;
    private final MaterialParseResultMapper parseResultMapper;
    private final RedisUtil redisUtil;

    @Async
    @Override
    public void generateGraphAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.GRAPH,
                "Generate Chinese knowledge graph JSON with materialId, nodes[{id,name}], edges[{sourceId,targetId}] for: " + fileUrl);
    }

    @Async
    @Override
    public void generateNoteAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.NOTE,
                "Generate concise Chinese markdown study notes for: " + fileUrl);
    }

    @Async
    @Override
    public void parseAudioAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.AUDIO,
                "Generate Chinese audio parse JSON with materialId, transcript, segments, keywords for: " + fileUrl);
    }

    @Async
    @Override
    public void parseVideoAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.VIDEO,
                "Generate Chinese video parse JSON with materialId, transcript, chapters, keywords, duration for: " + fileUrl);
    }

    @Async
    @Override
    public void parseExerciseAsync(Long taskId, String fileUrl, Long batchId) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            String response = stripCodeFence(callAi("Parse exercises from this file URL: " + fileUrl
                    + ". Output Chinese content and only a JSON array. Each item must contain type, content, options, answer, explanation, knowledgeTags."));
            List<ExerciseItem> items = JSON.parseArray(response, ExerciseItem.class);
            redisUtil.set("exercise:preview:" + taskId, items == null ? Collections.emptyList() : items);
            markTask(taskId, ParseStatus.DONE, null);
        } catch (RuntimeException e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    @Async
    @Override
    public void parseAnswerAsync(Long taskId, String fileUrl, Long answerBatchId) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            String response = stripCodeFence(callAi("Parse answers from this file URL: " + fileUrl
                    + ". Output only one Chinese JSON object with answerBatchId, exerciseItemId, answer array, and explanation."));
            AnswerItem item = JSON.parseObject(response, AnswerItem.class);
            if (item != null) {
                item.setAnswerBatchId(answerBatchId);
                redisUtil.set("answer:preview:" + taskId, item);
            }
            markTask(taskId, ParseStatus.DONE, null);
        } catch (RuntimeException e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ExerciseItem> getExercisePreview(Long taskId) {
        List<ExerciseItem> items = redisUtil.get("exercise:preview:" + taskId, List.class);
        return items == null ? Collections.emptyList() : items;
    }

    @Override
    public AnswerItem getAnswerPreview(Long taskId) {
        return redisUtil.get("answer:preview:" + taskId, AnswerItem.class);
    }

    @Async
    @Override
    public void gradeAsync(Long taskId, Long exerciseId) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            redisUtil.set("grade:result:" + taskId, stripCodeFence(callAi(
                    "Grade exercise " + exerciseId + ". Output Chinese JSON with score, correct, explanation, and feedback.")));
            markTask(taskId, ParseStatus.DONE, null);
        } catch (RuntimeException e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public ExerciseClassReportVO getClassReport(Long exerciseId) {
        ExerciseClassReportVO report = redisUtil.get("exercise:report:" + exerciseId, ExerciseClassReportVO.class);
        if (report != null) {
            return report;
        }
        report = new ExerciseClassReportVO();
        report.setSubmittedCount(0);
        report.setNotSubmittedCount(0);
        report.setAvgScore(0D);
        report.setAvgAccuracy(0D);
        report.setExerciseReports(new ArrayList<>());
        report.setStudentGrades(new ArrayList<>());
        return report;
    }

    @Override
    public String generateTeachingSuggestion(Long classId, String portraitSummary) {
        return callAi("Generate concise Chinese teaching suggestions in markdown for class " + classId
                + ". Include priority knowledge points, teaching actions, and students needing attention.\n" + portraitSummary);
    }

    private void runMaterialTask(Long taskId, Long materialId, TaskType type, String prompt) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            String response = stripCodeFence(callAi(prompt));
            parseResultMapper.insert(MaterialParseResult.builder()
                    .materialId(materialId)
                    .resultType(type.name())
                    .resultData(response)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build());
            markTask(taskId, ParseStatus.DONE, null);
        } catch (RuntimeException e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    private void markTask(Long taskId, ParseStatus status, String failReason) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            return;
        }
        task.setStatus(status);
        task.setFailReason(failReason);
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    private String callAi(String prompt) {
        String apiKey = firstNotBlank(System.getProperty("AI_API_KEY"), System.getenv("AI_API_KEY"));
        if (isBlank(apiKey)) {
            throw new IllegalStateException("AI_API_KEY is required");
        }
        String baseUrl = firstNotBlank(System.getProperty("AI_BASE_URL"), System.getenv("AI_BASE_URL"), DEFAULT_BASE_URL);
        String model = firstNotBlank(System.getProperty("AI_MODEL"), System.getenv("AI_MODEL"), DEFAULT_MODEL);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(trimRight(baseUrl, "/") + "/chat/completions").openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            byte[] body = buildPayload(model, prompt).getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            int status = connection.getResponseCode();
            String response = readBody(status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream());
            if (status < 200 || status >= 300) {
                throw new IllegalStateException("AI request failed, HTTP " + status + ": " + response);
            }
            String content = JSON.parseObject(response).getJSONArray("choices")
                    .getJSONObject(0).getJSONObject("message").getString("content");
            if (isBlank(content)) {
                throw new IllegalStateException("AI response is empty");
            }
            return content;
        } catch (IOException e) {
            throw new IllegalStateException("AI request error: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String buildPayload(String model, String prompt) {
        return "{\"model\":\"" + escapeJson(model) + "\",\"temperature\":0.2,\"messages\":["
                + "{\"role\":\"system\",\"content\":\"You are a learning assistant. Return structured output usable by code.\"},"
                + "{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}]}";
    }

    private String readBody(InputStream input) throws IOException {
        if (input == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private String stripCodeFence(String value) {
        if (value == null) {
            return "";
        }
        String text = value.trim();
        if (!text.startsWith("```")) {
            return text;
        }
        int firstLine = text.indexOf('\n');
        int lastFence = text.lastIndexOf("```");
        return firstLine >= 0 && lastFence > firstLine ? text.substring(firstLine + 1, lastFence).trim() : text;
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return "";
    }

    private String trimRight(String value, String suffix) {
        while (value.endsWith(suffix)) {
            value = value.substring(0, value.length() - suffix.length());
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
