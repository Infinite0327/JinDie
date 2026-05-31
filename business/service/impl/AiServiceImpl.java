package kdec.apple.cloud.app.business.service.impl;

import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.common.entity.ExerciseItem;
import kdec.apple.cloud.app.common.enums.ExerciseType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AiServiceImpl implements AiService {
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final ConcurrentMap<Long, String> TASK_RESULT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, List<ExerciseItem>> EXERCISE_PREVIEW_CACHE = new ConcurrentHashMap<>();

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
    public String generateChapterExercises(Long chapterId, String chapterTitle, String chapterContent, Integer questionCount) {
        requireText(chapterContent, "chapterContent");

        int count = questionCount == null || questionCount <= 0 ? 5 : questionCount;
        String prompt = "Generate exercises for the chapter below. Output Chinese content. "
                + "Output only a JSON array, no markdown fence. "
                + "Allowed types: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, SHORT_ANSWER. "
                + "Each item must contain type, content, options, answer, explanation, knowledgeTags. "
                + "options must be an array like {\"key\":\"A\",\"value\":\"option text\"}. "
                + "answer must be an array. Explanations should be concise and knowledgeTags should be accurate.\n\n"
                + "Question count: " + count + "\n"
                + "Chapter ID: " + valueOf(chapterId) + "\n"
                + "Chapter title: " + safeText(chapterTitle) + "\n"
                + "Chapter content:\n" + chapterContent;

        return stripCodeFence(callAi(prompt));
    }

    @Override
    public void generateGraphAsync(Long taskId, String fileUrl, Long materialId) {
        String prompt = "Generate a knowledge graph for this material URL: " + safeText(fileUrl) + ". "
                + "Output Chinese content. Output only JSON with materialId, nodes[{id,name}], edges[{sourceId,targetId}]. "
                + "materialId=" + valueOf(materialId);
        cacheTaskResult(taskId, stripCodeFence(callAi(prompt)));
    }

    @Override
    public void generateNoteAsync(Long taskId, String fileUrl, Long materialId) {
        String prompt = "Generate concise Chinese study notes in markdown for this material URL: "
                + safeText(fileUrl) + ". materialId=" + valueOf(materialId);
        cacheTaskResult(taskId, callAi(prompt));
    }

    @Override
    public void parseAudioAsync(Long taskId, String fileUrl, Long materialId) {
        String prompt = "Parse this audio material URL and output Chinese JSON with transcript, segments, and keywords: "
                + safeText(fileUrl) + ". materialId=" + valueOf(materialId);
        cacheTaskResult(taskId, stripCodeFence(callAi(prompt)));
    }

    @Override
    public void parseVideoAsync(Long taskId, String fileUrl, Long materialId) {
        String prompt = "Parse this video material URL and output Chinese JSON with transcript, chapters, keywords, and duration: "
                + safeText(fileUrl) + ". materialId=" + valueOf(materialId);
        cacheTaskResult(taskId, stripCodeFence(callAi(prompt)));
    }

    @Override
    public void parseExerciseAsync(Long taskId, String fileUrl, Long batchId) {
        String prompt = "Parse or generate exercises from this exercise file URL: " + safeText(fileUrl) + ". "
                + "Output Chinese content. Output only a JSON array. "
                + "Each item must contain type, content, options, answer, explanation, knowledgeTags. "
                + "Allowed types: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, SHORT_ANSWER. "
                + "batchId=" + valueOf(batchId);
        String result = stripCodeFence(callAi(prompt));
        cacheTaskResult(taskId, result);
        if (taskId != null) {
            EXERCISE_PREVIEW_CACHE.put(taskId, toExercisePreview(batchId, result));
        }
    }

    @Override
    public List<ExerciseItem> getExercisePreview(Long taskId) {
        List<ExerciseItem> items = EXERCISE_PREVIEW_CACHE.get(taskId);
        return items == null ? Collections.emptyList() : items;
    }

    @Override
    public String getAnswerPreview(Long taskId) {
        String result = TASK_RESULT_CACHE.get(taskId);
        return result == null ? "" : result;
    }

    @Override
    public void gradeAsync(Long taskId, Long exerciseId) {
        String prompt = "Grade exercise " + valueOf(exerciseId)
                + ". Output Chinese JSON with score, correct, correctAnswer, explanation, and aiFeedback.";
        cacheTaskResult(taskId, stripCodeFence(callAi(prompt)));
    }

    @Override
    public String getClassReport(Long exerciseId) {
        String prompt = "Generate a Chinese class exercise report for exercise " + valueOf(exerciseId)
                + ". Output JSON with submittedCount, notSubmittedCount, avgScore, avgAccuracy, weakKnowledgeTags, and suggestions.";
        return stripCodeFence(callAi(prompt));
    }

    @Override
    public String generateTeachingSuggestion(Long classId, String portraitSummary) {
        String prompt = "Generate concise Chinese teaching suggestions in markdown for class " + valueOf(classId) + ". "
                + "Include priority knowledge points, teaching actions, and students who may need attention. "
                + "Use the following portrait statistics:\n" + safeText(portraitSummary);
        return callAi(prompt);
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
        if (colonIndex < 0) {
            return "";
        }

        int quoteIndex = json.indexOf('"', colonIndex + 1);
        if (quoteIndex < 0) {
            return "";
        }

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

    private List<ExerciseItem> toExercisePreview(Long batchId, String aiJson) {
        List<ExerciseItem> result = new ArrayList<>();
        ExerciseItem item = new ExerciseItem();
        item.setId(System.currentTimeMillis());
        item.setBatchId(batchId);
        item.setType(ExerciseType.SHORT_ANSWER);
        item.setContent("AI generated exercise preview. Use rawJson to import detailed items.");
        item.setOptions("[]");
        item.setAnswer("[]");
        item.setExplanation("The raw AI result is cached by taskId.");
        item.setKnowledgeTags("[\"AI_PARSE\"]");
        result.add(item);
        return result;
    }

    private void cacheTaskResult(Long taskId, String result) {
        if (taskId != null) {
            TASK_RESULT_CACHE.put(taskId, result == null ? "" : result);
        }
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
