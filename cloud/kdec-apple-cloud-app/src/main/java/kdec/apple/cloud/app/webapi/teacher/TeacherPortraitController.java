package kdec.apple.cloud.app.webapi.teacher;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kd.bos.workflow.engine.TaskService;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.teacher.TeacherPortraitService;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.portrait.*;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
//TODO:做这个。
/**
 * 教师端 - 学生画像 & 班级画像 & AI教学建议
 */
@Api(tags = "教师端-学习画像")
@RestController
@RequestMapping("/teacher/classes/{classId}")
@RequiredArgsConstructor
public class TeacherPortraitController {

    private final TeacherPortraitService portraitService;
    private final TaskService taskService;

    // ========================================================
    //  一、学生个人画像
    // ========================================================

    /**
     * 获取学生画像总览
     * 包含：学习时长统计、完成题目数、正确率趋势、最近学习内容
     */
    @ApiOperation("获取学生画像总览")
    @GetMapping("/students/{studentId}/portrait")
    public Result<StudentPortraitVO> getStudentPortrait(@PathVariable Long classId,
                                                        @PathVariable Long studentId) {
        return Result.ok(portraitService.getStudentPortrait(classId, studentId));
    }

    /**
     * 获取学生学习时长统计
     * granularity: DAY（日历打圈）/ MONTH（热度图）/ YEAR（年度汇总）
     */
    @ApiOperation("获取学生学习时长统计")
    @GetMapping("/students/{studentId}/portrait/duration")
    public Result<StudyDurationVO> getStudyDuration(@PathVariable Long classId,
                                                    @PathVariable Long studentId,
                                                    @RequestParam String granularity,
                                                    @RequestParam(required = false) String yearMonth) {
        return Result.ok(portraitService.getStudyDuration(studentId, granularity, yearMonth));
    }

    /**
     * 获取学生正确率趋势
     */
    @ApiOperation("获取学生正确率趋势")
    @GetMapping("/students/{studentId}/portrait/accuracy-trend")
    public Result<AccuracyTrendVO> getAccuracyTrend(@PathVariable Long classId,
                                                    @PathVariable Long studentId,
                                                    @RequestParam(defaultValue = "30") Integer days) {
        return Result.ok(portraitService.getAccuracyTrend(studentId, days));
    }


    /**
     * 获取学生薄弱知识点
     */
    @ApiOperation("获取学生薄弱知识点")
    @GetMapping("/students/{studentId}/portrait/weak-points")
    public Result<WeakPointVO> getWeakPoints(@PathVariable Long classId,
                                             @PathVariable Long studentId) {
        return Result.ok(portraitService.getWeakPoints(studentId));
    }



    // ========================================================
    //  二、班级整体画像
    // ========================================================

    /**
     * 获取班级画像总览
     * 包含：平均学习时长、平均正确率、整体完成情况
     */
    @ApiOperation("获取班级画像总览")
    @GetMapping("/portrait")
    public Result<ClassPortraitVO> getClassPortrait(@PathVariable Long classId) {
        return Result.ok(portraitService.getClassPortrait(classId));
    }

    /**
     * 获取班级错题分布
     */
    @ApiOperation("获取班级错题分布")
    @GetMapping("/portrait/wrong-answer-distribution")
    public Result<WrongAnswerDistributionVO> getWrongAnswerDistribution(@PathVariable Long classId) {
        return Result.ok(portraitService.getWrongAnswerDistribution(classId));
    }

    /**
     * 获取班级薄弱知识点热力图
     */
    @ApiOperation("获取班级薄弱知识点热力图")
    @GetMapping("/portrait/weak-knowledge-heatmap")
    public Result<ClassWeakKnowledgeVO> getWeakKnowledgeHeatmap(@PathVariable Long classId) {
        return Result.ok(portraitService.getWeakKnowledgeHeatmap(classId));
    }


    // ========================================================
    //  三、AI教学建议
    // ========================================================

    /**
     * 触发AI根据班级画像生成教学建议（异步）
     */
    @ApiOperation("触发AI生成教学建议")
    @PostMapping("/portrait/teaching-suggestion/generate")
    public Result<TaskCreatedVO> generateTeachingSuggestion(@PathVariable Long classId) {
        return Result.ok(portraitService.generateTeachingSuggestion(classId));
    }

    /**
     * 轮询教学建议生成状态
     */
    @ApiOperation("轮询教学建议生成状态")
    @GetMapping("/portrait/teaching-suggestion/tasks/{taskId}/status")
    public Result<TaskStatusVO> getTeachingSuggestionStatus(
            @PathVariable Long classId,
            @PathVariable Long taskId) {
        return Result.ok(taskService.getTaskStatus(taskId));
    }

    /**
     * 获取AI教学建议结果
     */
    @ApiOperation("获取AI教学建议")
    @GetMapping("/portrait/teaching-suggestion")
    public Result<TeachingSuggestionVO> getTeachingSuggestion(@PathVariable Long classId) {
        return Result.ok(portraitService.getTeachingSuggestion(classId));
    }
}
