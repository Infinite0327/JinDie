package kdec.apple.cloud.app.webapi.ai;

import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.AiService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {
    private final AiService aiService;

    @PostMapping("/teaching-suggestion")
    public Result<String> generateTeachingSuggestion(@RequestBody TeachingSuggestionRequest request) {
        return Result.ok(aiService.generateTeachingSuggestion(request.getClassId(), request.getPortraitSummary()));
    }

    @Data
    public static class TeachingSuggestionRequest {
        private Long classId;
        private String portraitSummary;
    }
}
