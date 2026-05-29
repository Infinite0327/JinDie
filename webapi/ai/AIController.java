package kdec.apple.cloud.app.webapi.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIController {
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_MODEL = "gpt-4o-mini";

    public MindMapResponse generateMindMap(MindMapRequest request) {
        if (request == null || isBlank(request.getChapterContent())) {
            return MindMapResponse.fail("chapterContent is required");
        }

        String prompt = "Generate a Mermaid mindmap for the chapter below. "
                + "Output Chinese content. Output only Mermaid code, no markdown fence. "
                + "Use the chapter title as root node. Include core concepts, relations, common mistakes, and learning goals.\n\n"
                + "Chapter ID: " + valueOf(request.getChapterId()) + "\n"
                + "Chapter title: " + safeText(request.getChapterTitle()) + "\n"
                + "Chapter content:\n" + request.getChapterContent();

        return MindMapResponse.ok(callAi(prompt));
    }

    public ExerciseResponse generateExercises(ExerciseRequest request) {
        if (request == null || isBlank(request.getChapterContent())) {
            return ExerciseResponse.fail("chapterContent is required");
        }

        int count = request.getQuestionCount() == null || request.getQuestionCount() <= 0
                ? 5
                : request.getQuestionCount();

        String prompt = "Generate exercises for the chapter below. Output Chinese content. "
                + "Output only a JSON array, no markdown fence. "
                + "Allowed types: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, SHORT_ANSWER. "
                + "Each item must contain type, content, options, answer, explanation, knowledgeTags. "
                + "options must be an array like {\"key\":\"A\",\"value\":\"option text\"}. "
                + "answer must be an array. Explanations should be concise and knowledgeTags should be accurate.\n\n"
                + "Question count: " + count + "\n"
                + "Chapter ID: " + valueOf(request.getChapterId()) + "\n"
                + "Chapter title: " + safeText(request.getChapterTitle()) + "\n"
                + "Chapter content:\n" + request.getChapterContent();

        return ExerciseResponse.ok(callAi(prompt));
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

            String payload = buildChatPayload(model, userPrompt);
            byte[] body = payload.getBytes(StandardCharsets.UTF_8);
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

    public static class MindMapRequest {
        private Long chapterId;
        private String chapterTitle;
        private String chapterContent;

        public Long getChapterId() {
            return chapterId;
        }

        public void setChapterId(Long chapterId) {
            this.chapterId = chapterId;
        }

        public String getChapterTitle() {
            return chapterTitle;
        }

        public void setChapterTitle(String chapterTitle) {
            this.chapterTitle = chapterTitle;
        }

        public String getChapterContent() {
            return chapterContent;
        }

        public void setChapterContent(String chapterContent) {
            this.chapterContent = chapterContent;
        }
    }

    public static class ExerciseRequest extends MindMapRequest {
        private Integer questionCount;

        public Integer getQuestionCount() {
            return questionCount;
        }

        public void setQuestionCount(Integer questionCount) {
            this.questionCount = questionCount;
        }
    }

    public static class MindMapResponse {
        private Boolean success;
        private String message;
        private String mindMap;

        public static MindMapResponse ok(String mindMap) {
            MindMapResponse response = new MindMapResponse();
            response.setSuccess(true);
            response.setMessage("success");
            response.setMindMap(mindMap);
            return response;
        }

        public static MindMapResponse fail(String message) {
            MindMapResponse response = new MindMapResponse();
            response.setSuccess(false);
            response.setMessage(message);
            response.setMindMap("");
            return response;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMindMap() {
            return mindMap;
        }

        public void setMindMap(String mindMap) {
            this.mindMap = mindMap;
        }
    }

    public static class ExerciseResponse {
        private Boolean success;
        private String message;
        private String exercisesJson;

        public static ExerciseResponse ok(String exercisesJson) {
            ExerciseResponse response = new ExerciseResponse();
            response.setSuccess(true);
            response.setMessage("success");
            response.setExercisesJson(exercisesJson);
            return response;
        }

        public static ExerciseResponse fail(String message) {
            ExerciseResponse response = new ExerciseResponse();
            response.setSuccess(false);
            response.setMessage(message);
            response.setExercisesJson("[]");
            return response;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getExercisesJson() {
            return exercisesJson;
        }

        public void setExercisesJson(String exercisesJson) {
            this.exercisesJson = exercisesJson;
        }
    }
}
