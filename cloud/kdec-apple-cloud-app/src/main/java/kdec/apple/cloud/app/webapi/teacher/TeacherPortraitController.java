package kdec.apple.cloud.app.webapi.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.teacher.TeacherPortraitService;
import kdec.apple.cloud.app.common.dto.portrait.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Teacher learning portrait")
@RestController
@RequestMapping("/teacher/classes/{classId}")
@RequiredArgsConstructor
public class TeacherPortraitController {
    private final TeacherPortraitService portraitService;

    @ApiOperation("Get student portrait")
    @GetMapping("/students/{studentId}/portrait")
    public Result<StudentPortraitVO> getStudentPortrait(@PathVariable Long classId, @PathVariable Long studentId) {
        return Result.ok(portraitService.getStudentPortrait(classId, studentId));
    }

    @ApiOperation("Get study duration")
    @GetMapping("/students/{studentId}/portrait/duration")
    public Result<StudyDurationVO> getStudyDuration(@PathVariable Long studentId, @RequestParam String granularity,
                                                    @RequestParam(required = false) String yearMonth) {
        return Result.ok(portraitService.getStudyDuration(studentId, granularity, yearMonth));
    }

    @ApiOperation("Get accuracy trend")
    @GetMapping("/students/{studentId}/portrait/accuracy-trend")
    public Result<AccuracyTrendVO> getAccuracyTrend(@PathVariable Long studentId,
                                                    @RequestParam(defaultValue = "30") Integer days) {
        return Result.ok(portraitService.getAccuracyTrend(studentId, days));
    }

    @ApiOperation("Get weak points")
    @GetMapping("/students/{studentId}/portrait/weak-points")
    public Result<WeakPointVO> getWeakPoints(@PathVariable Long studentId) {
        return Result.ok(portraitService.getWeakPoints(studentId));
    }

    @ApiOperation("Get class portrait")
    @GetMapping("/portrait")
    public Result<ClassPortraitVO> getClassPortrait(@PathVariable Long classId) {
        return Result.ok(portraitService.getClassPortrait(classId));
    }

    @ApiOperation("Get wrong answer distribution")
    @GetMapping("/portrait/wrong-answer-distribution")
    public Result<WrongAnswerDistributionVO> getWrongAnswerDistribution(@PathVariable Long classId) {
        return Result.ok(portraitService.getWrongAnswerDistribution(classId));
    }

    @ApiOperation("Get weak knowledge heatmap")
    @GetMapping("/portrait/weak-knowledge-heatmap")
    public Result<ClassWeakKnowledgeVO> getWeakKnowledgeHeatmap(@PathVariable Long classId) {
        return Result.ok(portraitService.getWeakKnowledgeHeatmap(classId));
    }

    @ApiOperation("Generate teaching suggestion")
    @PostMapping("/portrait/teaching-suggestion/generate")
    public Result<TaskCreatedVO> generateTeachingSuggestion(@PathVariable Long classId) {
        return Result.ok(portraitService.generateTeachingSuggestion(classId));
    }

    @ApiOperation("Get teaching suggestion task status")
    @GetMapping("/portrait/teaching-suggestion/tasks/{taskId}/status")
    public Result<TaskStatusVO> getTeachingSuggestionStatus(@PathVariable Long taskId) {
        return Result.ok(portraitService.getTeachingSuggestionStatus(taskId));
    }

    @ApiOperation("Get teaching suggestion")
    @GetMapping("/portrait/teaching-suggestion")
    public Result<TeachingSuggestionVO> getTeachingSuggestion(@PathVariable Long classId) {
        return Result.ok(portraitService.getTeachingSuggestion(classId));
    }
}
