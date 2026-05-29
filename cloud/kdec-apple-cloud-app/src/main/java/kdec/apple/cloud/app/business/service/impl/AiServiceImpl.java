package kdec.apple.cloud.app.business.service.impl;

import com.alibaba.fastjson.JSON;
import kdec.apple.cloud.app.business.mapper.MaterialParseResultMapper;
import kdec.apple.cloud.app.business.mapper.TaskMapper;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.common.dto.answer.AnswerItem;
import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseClassReportVO;
import kdec.apple.cloud.app.common.dto.graph.GraphVO;
import kdec.apple.cloud.app.common.dto.portrait.TeachingSuggestionVO;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";

    private static final ConcurrentMap<Long, List<ExerciseItem>> EXERCISE_PREVIEW_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, AnswerItem> ANSWER_PREVIEW_CACHE = new ConcurrentHashMap<>();

    private final TaskMapper taskMapper;
    private final MaterialParseResultMapper parseResultMapper;

    @Override
    public String generateChapterMindMap(Long chapterId, String chapterTitle, String chapterContent) {
        requireText(chapterContent, "chapterContent");
        String prompt = "Generate a Mermaid mindmap for the chapter below. "
                + "Output Chinese content. Output only Mermaid code, no markdown fence. "
                + "Use the chapter title as root node. Include core concepts, relations, common mistakes, and learning goals.\n\n"
                + "Chapter ID: " + valueOf(chapterId) + "\n"
                + "Chapter title: " + safeText(chapterTitle) + "\n"
                + "Chapter content:\n" + chapterContent;
        return callAi(prompt);
    }

    @Override
    public List<ExerciseItem> generateChapterExercises(Long chapterId, String chapterTitle, String chapterContent, Integer questionCount) {
        requireText(chapterContent, "chapterContent");
        int count = questionCount == null || questionCount <= 0 ? 5 : questionCount;
        String prompt = exercisePrompt(chapterId, chapterTitle, chapterContent, count);
        String response = stripCodeFence(callAi(prompt));
        List<ExerciseItem> items = JSON.parseArray(response, ExerciseItem.class);
        return items == null ? Collections.emptyList() : items;
    }

    @Async
    @Override
    public void generateGraphAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.GRAPH, "Generate a knowledge graph JSON for this material URL: " + fileUrl
                + ". Output GraphVO-compatible JSON with materialId, nodes[{id,name}], edges[{sourceId,targetId}].");
    }

    @Async
    @Override
    public void generateNoteAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.NOTE, "Generate concise Chinese study notes in markdown for this material URL: " + fileUrl);
    }

    @Async
    @Override
    public void parseAudioAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.AUDIO, "Parse this audio URL and output AudioParseResultVO-compatible JSON: " + fileUrl);
    }

    @Async
    @Override
    public void parseVideoAsync(Long taskId, String fileUrl, Long materialId) {
        runMaterialTask(taskId, materialId, TaskType.VIDEO, "Parse this video URL and output VideoParseResultVO-compatible JSON: " + fileUrl);
    }

    @Async
    @Override
    public void parseExerciseAsync(Long taskId, String fileUrl, Long batchId) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            String prompt = "Parse or generate exercises from this exercise file URL: " + fileUrl + ". "
                    + "Output Chinese content. Output only a JSON array. "
                    + "Each item must match ExerciseItem fields: type, content, options, answer, explanation, knowledgeTags. "
                    + "options, answer, and knowledgeTags should be JSON strings if needed by the database entity.";
            String response = stripCodeFence(callAi(prompt));
            List<ExerciseItem> items = JSON.parseArray(response, ExerciseItem.class);
            EXERCISE_PREVIEW_CACHE.put(taskId, items == null ? Collections.emptyList() : items);
            markTask(taskId, ParseStatus.DONE, null);
        } catch (Exception e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public List<ExerciseItem> getExercisePreview(Long taskId) {
        List<ExerciseItem> items = EXERCISE_PREVIEW_CACHE.get(taskId);
        return items == null ? Collections.emptyList() : items;
    }

    @Override
    public AnswerItem getAnswerPreview(Long taskId) {
        return ANSWER_PREVIEW_CACHE.get(taskId);
    }

    @Async
    @Override
    public void gradeAsync(Long taskId, Long exerciseId) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            callAi("Grade exercise " + exerciseId + " and output concise Chinese feedback.");
            markTask(taskId, ParseStatus.DONE, null);
        } catch (Exception e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public ExerciseClassReportVO getClassReport(Long exerciseId) {
        return new ExerciseClassReportVO();
    }

    @Override
    public TeachingSuggestionVO generateTeachingSuggestion(Long classId) {
        TeachingSuggestionVO vo = new TeachingSuggestionVO();
        vo.setClassId(classId);
        vo.setContent(callAi("Generate Chinese teaching suggestions for class " + classId + ". Output markdown."));
        vo.setSuggestedKnowledgeTags(new ArrayList<>());
        vo.setFocusStudents(new ArrayList<>());
        vo.setGeneratedAt(LocalDateTime.now());
        return vo;
    }

    @Override
    public GraphVO generateGraph(Long materialId, String sourceText) {
        String response = stripCodeFence(callAi("Generate GraphVO-compatible JSON in Chinese from this source. "
                + "Output only JSON with materialId, nodes, edges. materialId=" + materialId + "\n" + sourceText));
        GraphVO graph = JSON.parseObject(response, GraphVO.class);
        if (graph != null) {
            graph.setMaterialId(materialId);
        }
        return graph;
    }

    private void runMaterialTask(Long taskId, Long materialId, TaskType taskType, String prompt) {
        markTask(taskId, ParseStatus.PROCESSING, null);
        try {
            String response = stripCodeFence(callAi(prompt));
            MaterialParseResult result = MaterialParseResult.builder()
                    .materialId(materialId)
                    .resultType(taskType.name())
                    .resultData(response)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            parseResultMapper.insert(result);
            markTask(taskId, ParseStatus.DONE, null);
        } catch (Exception e) {
            markTask(taskId, ParseStatus.FAILED, e.getMessage());
        }
    }

    private void markTask(Long taskId, ParseStatus status, String failReason) {
        if (taskId == null || taskMapper == null) {
            return;
        }
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            return;
        }
        task.setStatus(status);
        task.setFailReason(failReason);
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    private String exercisePrompt(Long chapterId, String chapterTitle, String chapterContent, int count) {
        return "Generate exercises for the chapter below. Output Chinese content. "
                + "Output only a JSON array, no markdown fence. "
                + "Allowed types: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, SHORT_ANSWER. "
                + "Each item must contain type, content, options, answer, explanation, knowledgeTags. "
                + "Because ExerciseItem stores options/answer/knowledgeTags as String, make those three fields JSON strings. "
                + "Question count: " + count + "\n"
                + "Chapter ID: " + valueOf(chapterId) + "\n"
                + "Chapter title: " + safeText(chapterTitle) + "\n"
                + "Chapter content:\n" + chapterContent;
    }

    private String callAi(String userPrompt) {
        String apiKey = firstNotBlank(System.getProperty("AI_API_KEY"), System.getenv("AI_API_KEY"));
        if (isBlank(apiKey)) {
            throw new IllegalStateException("AI_API_KEY is required. Set env AI_API_KEY or JVM arg -DAI_API_KEY.");
        }

        String baseUrl = firstNotBlank(System.getProperty("AI_BASE_URL"), System.getenv("AI_BASE_URL"), DEFAULT_BASE_URL);
        String model = firstNotBlank(System.getProperty("AI_MODEL"), System.getenv("AI_MODEL"), DEFAULT_MODEL);
        String endpoint = trimRight(baseUrl, "/") + "/chat/completions";

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            byte[] body = buildChatPayload(model, userPrompt).getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(body.length));
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body);
            }

            int statusCode = connection.getResponseCode();
            String responseBody = readBody(statusCode >= 200 && statusCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());
            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException("AI request failed, HTTP " + statusCode + ": " + responseBody);
            }
            String content = extractMessageContent(responseBody);
            if (isBlank(content)) {
                throw new IllegalStateException("AI response content is empty: " + responseBody);
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

    private String buildChatPayload(String model, String userPrompt) {
        return "{"
                + "\"model\":\"" + escapeJson(model) + "\","
                + "\"temperature\":0.2,"
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"You are a learning assistant for an education app. Return structured output that can be used by code directly.\"},"
                + "{\"role\":\"user\",\"content\":\"" + escapeJson(userPrompt) + "\"}"
                + "]"
                + "}";
    }

    private String extractMessageContent(String json) {
        String marker = "\"content\"";
        int markerIndex = json.indexOf(marker);
        if (markerIndex < 0) {
            return "";
        }
        int colonIndex = json.indexOf(':', markerIndex + marker.length());
        int quoteIndex = json.indexOf('"', colonIndex + 1);
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (int i = quoteIndex + 1; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (escaped) {
                appendEscaped(result, ch);
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else if (ch == '"') {
                return result.toString();
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private void appendEscaped(StringBuilder result, char ch) {
        if (ch == 'n') {
            result.append('\n');
        } else if (ch == 'r') {
            result.append('\r');
        } else if (ch == 't') {
            result.append('\t');
        } else if (ch == '"' || ch == '\\' || ch == '/') {
            result.append(ch);
        } else {
            result.append('\\').append(ch);
        }
    }

    private String readBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '"') {
                builder.append("\\\"");
            } else if (ch == '\\') {
                builder.append("\\\\");
            } else if (ch == '\n') {
                builder.append("\\n");
            } else if (ch == '\r') {
                builder.append("\\r");
            } else if (ch == '\t') {
                builder.append("\\t");
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    private String stripCodeFence(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }
        int firstLineEnd = trimmed.indexOf('\n');
        int lastFence = trimmed.lastIndexOf("```");
        if (firstLineEnd >= 0 && lastFence > firstLineEnd) {
            return trimmed.substring(firstLineEnd + 1, lastFence).trim();
        }
        return trimmed;
    }

    private void requireText(String text, String name) {
        if (isBlank(text)) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private static String firstNotBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return "";
    }

    private static String trimRight(String text, String suffix) {
        if (text == null) {
            return "";
        }
        while (text.endsWith(suffix)) {
            text = text.substring(0, text.length() - suffix.length());
        }
        return text;
    }

    private static String valueOf(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static String safeText(String text) {
        return text == null ? "" : text;
    }
}
