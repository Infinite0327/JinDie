package kdec.apple.cloud.app.webapi.ai;

import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.entity.ExerciseItem;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {
    private final AiService aiService;

    @PostMapping("/chapters/mind-map")
    public Result<MindMapResponse> generateMindMap(@RequestBody ChapterAiRequest request) {
        String mindMap = aiService.generateChapterMindMap(
                request.getChapterId(),
                request.getChapterTitle(),
                request.getChapterContent()
        );
        MindMapResponse response = new MindMapResponse();
        response.setChapterId(request.getChapterId());
        response.setMindMap(mindMap);
        return Result.ok(response);
    }

    @PostMapping("/chapters/exercises")
    public Result<ExerciseResponse> generateExercises(@RequestBody ChapterAiRequest request) {
        List<ExerciseItem> exercises = aiService.generateChapterExercises(
                request.getChapterId(),
                request.getChapterTitle(),
                request.getChapterContent(),
                request.getQuestionCount()
        );
        ExerciseResponse response = new ExerciseResponse();
        response.setChapterId(request.getChapterId());
        response.setExercises(exercises);
        return Result.ok(response);
    }

    @Data
    public static class ChapterAiRequest {
        private Long chapterId;
        private String chapterTitle;
        private String chapterContent;
        private Integer questionCount;
    }

    @Data
    public static class MindMapResponse {
        private Long chapterId;
        private String mindMap;
    }

    @Data
    public static class ExerciseResponse {
        private Long chapterId;
        private List<ExerciseItem> exercises;
    }
}
