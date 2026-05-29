package kdec.apple.cloud.app.webapi.ai;

import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.business.service.impl.AiServiceImpl;

public class AIController {
    private final AiService aiService = new AiServiceImpl();

    public MindMapResponse generateMindMap(MindMapRequest request) {
        if (request == null || isBlank(request.getChapterContent())) {
            return MindMapResponse.fail("chapterContent is required");
        }

        String mindMap = aiService.generateChapterMindMap(
                request.getChapterId(),
                request.getChapterTitle(),
                request.getChapterContent()
        );
        return MindMapResponse.ok(mindMap);
    }

    public ExerciseResponse generateExercises(ExerciseRequest request) {
        if (request == null || isBlank(request.getChapterContent())) {
            return ExerciseResponse.fail("chapterContent is required");
        }

        String exercisesJson = aiService.generateChapterExercises(
                request.getChapterId(),
                request.getChapterTitle(),
                request.getChapterContent(),
                request.getQuestionCount()
        );
        return ExerciseResponse.ok(exercisesJson);
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
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
