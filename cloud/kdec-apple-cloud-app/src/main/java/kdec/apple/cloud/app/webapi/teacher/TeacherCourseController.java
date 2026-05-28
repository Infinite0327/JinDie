package kdec.apple.cloud.app.webapi.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kd.bos.workflow.engine.TaskService;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.MaterialService;
import kdec.apple.cloud.app.business.service.teacher.TeacherChapterService;
import kdec.apple.cloud.app.business.service.teacher.TeacherExerciseService;
import kdec.apple.cloud.app.common.dto.answer.AnswerBatchResultVO;
import kdec.apple.cloud.app.common.dto.answer.AnswerItem;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.answer.AnswerConfirmDTO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDTO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterSortDTO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.exercise.*;
import kdec.apple.cloud.app.common.dto.materials.MaterialUpdateDTO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUploadDTO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUploadVO;
import kdec.apple.cloud.app.common.dto.materials.MaterialVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import kdec.apple.cloud.app.entity.ExerciseBatch;
import kdec.apple.cloud.app.entity.ExerciseItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教师端 - 课程章节 / 习题 / 资料管理
 */
@Slf4j
@Api(tags = "教师端-课程管理")
@RestController
@RequestMapping("/teacher/courses/{classId}")
@RequiredArgsConstructor
public class TeacherCourseController {

    private final TeacherExerciseService exerciseService;
    private final MaterialService materialService;
    private final TeacherChapterService teacherChapterService;
    private final TaskService taskService;

    // ========================================================
    //  一、章节管理（增删改查）
    // ========================================================

    /**
     * 新增章节
     */
    @ApiOperation("新增章节")
    @PostMapping("/chapters")
    public Result<ChapterDetailVO> createChapter(@PathVariable Long courseId,
                                                 @RequestBody ChapterDTO dto) {
        dto.setCourseId(courseId);
        return Result.ok(teacherChapterService.createChapter(dto));
    }

    /**
     * 修改章节信息
     */
    @ApiOperation("修改章节")
    @PutMapping("/chapters/{chapterId}")
    public Result<Void> updateChapter(@PathVariable Long courseId,
                                      @PathVariable Long chapterId,
                                      @RequestBody ChapterDTO dto) {
        teacherChapterService.updateChapter(chapterId, dto);
        return Result.ok();
    }

    /**
     * 删除章节
     */
    @ApiOperation("删除章节")
    @DeleteMapping("/chapters/{chapterId}")
    public Result<Void> deleteChapter(@PathVariable Long courseId,
                                      @PathVariable Long chapterId) {
        teacherChapterService.deleteChapter(chapterId);
        return Result.ok();
    }

    /**
     * 获取课程章节列表
     */
    @ApiOperation("获取章节列表")
    @GetMapping("/chapters")
    public Result<List<ChapterDetailVO>> listChapters(@PathVariable Long courseId) {
        return Result.ok(teacherChapterService.listChapters(courseId));
    }

    /**
     * 章节排序
     */
    @ApiOperation("章节排序")
    @PutMapping("/chapters/sort")
    public Result<Void> sortChapters(@PathVariable Long courseId,
                                     @RequestBody ChapterSortDTO dto) {
        teacherChapterService.sortChapters(courseId, dto);
        return Result.ok();
    }

    // ========================================================
    //  二、课件资料管理（上传后自动同步到学生资料库）
    // ========================================================

    /**
     * 上传课件资料（自动同步至该课程所有班级学生）
     */
    @ApiOperation("上传课件资料")
    @PostMapping("/chapters/{chapterId}/materials")
    public Result<MaterialUploadVO> uploadMaterial(
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @RequestPart("file") MultipartFile file,
            @ModelAttribute MaterialUploadDTO dto) {
        dto.setCourseId(courseId);
        dto.setChapterId(chapterId);
        return Result.ok(materialService.upload(file, dto));
    }

    /**
     * 删除课件资料
     */
    @ApiOperation("删除课件资料")
    @DeleteMapping("/chapters/{chapterId}/materials/{materialId}")
    public Result<Void> deleteMaterial(@PathVariable Long courseId,
                                       @PathVariable Long chapterId,
                                       @PathVariable Long materialId) {
        materialService.delete(materialId);
        return Result.ok();
    }

    /**
     * 修改课件资料信息（标题、描述等）
     */
    @ApiOperation("修改课件资料")
    @PutMapping("/chapters/{chapterId}/materials/{materialId}")
    public Result<Void> updateMaterial(@PathVariable Long courseId,
                                       @PathVariable Long chapterId,
                                       @PathVariable Long materialId,
                                       @RequestBody MaterialUpdateDTO dto) {
        materialService.update(materialId, dto);
        return Result.ok();
    }

    /**
     * 获取章节课件列表
     */
    @ApiOperation("获取章节课件列表")
    @GetMapping("/chapters/{chapterId}/materials")
    public Result<List<MaterialVO>> listMaterials(@PathVariable Long courseId,
                                                  @PathVariable Long chapterId) {
        return Result.ok(materialService.listByChapter(courseId, chapterId));
    }


    //TODO:轮询路径是否全部一致？
    /**
     * 轮询任务状态（通用）
     */
    @GetMapping("/tasks/{taskId}/status")
    public Result<TaskStatusVO> getTaskStatus(
            @PathVariable Long classId,
            @PathVariable Long taskId) {
        return Result.ok(taskService.getTaskStatus(taskId));
    }

    // ========================================================
    //  三、习题管理（上传后自动同步到学生习题库）
    // ========================================================

    /**
     * 上传习题文件（自动解析同步至学生习题库）
     */
    @ApiOperation("上传习题")
    @PostMapping("/chapters/{chapterId}/exercises")
    public Result<TaskCreatedVO> uploadExercises(
            @PathVariable Long chapterId,
            @ModelAttribute ExerciseUploadDTO dto,
            @RequestPart("file") MultipartFile file) {
        return Result.ok(exerciseService.upload(dto, file));
    }

    /**
     * 获取AI解析结果（供老师预览修改）
     */
    @GetMapping("/chapters/{chapterId}/exercises/preview/{taskId}")
    public Result<List<ExerciseItem>> getExercisePreview(
            @PathVariable Long classId,
            @PathVariable Long chapterId,
            @PathVariable Long taskId) {
        return Result.ok(exerciseService.getPreview(taskId));
    }

    /**
     * 确认提交（老师修改后提交，写入数据库）
     */
    @PostMapping("/chapters/{chapterId}/exercises/confirm")
    public Result<ExerciseBatchResultVO> confirmExercises(
            @PathVariable Long classId,
            @PathVariable Long chapterId,
            @RequestBody ExerciseConfirmDTO dto) {
        return Result.ok(exerciseService.confirm(dto));
    }


    /**
     * 修改习题
     */
    @ApiOperation("修改习题")
    @PutMapping("/chapters/{chapterId}/exercises/{exerciseId}")
    public Result<Void> updateExercise(@PathVariable Long courseId,
                                       @PathVariable Long chapterId,
                                       @PathVariable Long exerciseId,
                                       @RequestBody ExerciseItem item) {
        exerciseService.update(exerciseId, item);
        return Result.ok();
    }

    /**
     * 删除习题
     */
    @ApiOperation("按批次删除习题")
    @DeleteMapping("/chapters/{chapterId}/exercises/{exerciseId}")
    public Result<Void> deleteExerciseBatch(@PathVariable Long courseId,
                                       @PathVariable Long chapterId,
                                       @PathVariable Long batchId) {
        exerciseService.deleteBatch(batchId);
        return Result.ok();
    }

    /**
     * 删除单道题
     * @param classId
     * @param chapterId
     * @param itemId
     * @return
     */
    @DeleteMapping("/chapters/{chapterId}/exercises/{itemId}")
    public Result<Void> deleteExerciseItem(
            @PathVariable Long classId,
            @PathVariable Long chapterId,
            @PathVariable Long itemId) {
        exerciseService.deleteExercise(itemId);
        return Result.ok();
    }

    /**
     * 获取章节下所有习题批次列表
     */
    @GetMapping("/chapters/{chapterId}/exercises/batches")
    public Result<List<ExerciseBatch>> listBatches(
            @PathVariable Long classId,
            @PathVariable Long chapterId) {
        return Result.ok(exerciseService.listBatchByChapter(chapterId));
    }
    /**
     * 获取批次习题列表
     */
    @ApiOperation("获取批次习题列表")
    @GetMapping("/chapters/{chapterId}/exercises/batches/{batchId}")
    public Result<List<ExerciseItem>> listExercises(@PathVariable Long courseId, @PathVariable Long chapterId, @PathVariable Long batchId) {
        return Result.ok(exerciseService.listByBatch(chapterId, batchId));
    }



    // ========================================================
    //  四、上传习题答案
    // ========================================================

    /**
     * 上传答案文件（异步解析）
     */
    @PostMapping("/chapters/{chapterId}/exercises/answer/upload")
    public Result<TaskCreatedVO> uploadAnswer(
            @PathVariable Long classId,
            @PathVariable Long chapterId,
            @RequestPart("file") MultipartFile file) {
        return Result.ok(exerciseService.uploadAnswer(classId, chapterId, file));
    }

    /**
     * 获取答案解析预览
     */
    @GetMapping("/chapters/{chapterId}/exercises/answer/preview/{taskId}")
    public Result<AnswerItem> getAnswerPreview(
            @PathVariable Long classId,
            @PathVariable Long chapterId,
            @PathVariable Long taskId) {
        log.info("获取答案解析预览");
        return Result.ok(exerciseService.getAnswerPreview(taskId));
    }

    /**
     * 确认提交答案
     */
    @PostMapping("/chapters/{chapterId}/exercises/answer/confirm")
    public Result<AnswerBatchResultVO> confirmAnswer(
            @RequestBody AnswerConfirmDTO dto) {
        return Result.ok(exerciseService.confirmAnswer(dto));
    }

    /**
     * 修改已确认的答案
     */
    @PutMapping("/chapters/{chapterId}/exercises/{exerciseId}/answer")
    public Result<Void> updateAnswer(
            @PathVariable Long classId,
            @PathVariable Long chapterId,
            @PathVariable Long itemId,
            @RequestBody AnswerItem item) {
        exerciseService.updateAnswer(itemId, item);
        return Result.ok();
    }

    /**
     * 删除所有答案
     */
    @DeleteMapping("/chapters/{chapterId}/exercises/{exerciseId}/answer")
    public Result<Void> deleteAnswer(
            @PathVariable Long classId,
            @PathVariable Long chapterId, @PathVariable Long batchId) {
        exerciseService.deleteAnswer(batchId);
        return Result.ok();
    }

    // ========================================================
    //  五，ai批改
    // ========================================================

    /**
     * 触发AI批改该题所有学生作业（异步）
     */
    @ApiOperation("触发AI批改")
    @PostMapping("/chapters/{chapterId}/exercises/{exerciseId}/grade")
    public Result<TaskCreatedVO> triggerAutoGrade(@PathVariable Long courseId,
                                                  @PathVariable Long chapterId,
                                                  @PathVariable Long exerciseId) {
        return Result.ok(exerciseService.triggerAutoGrade(exerciseId));
    }

    /**
     * 轮询批改任务状态
     */
    @ApiOperation("轮询批改任务状态")
    @GetMapping("/grade-tasks/{taskId}/status")
    public Result<GradeTaskStatusVO> getGradeTaskStatus(@PathVariable Long courseId,
                                                        @PathVariable Long taskId) {
        return Result.ok(exerciseService.getGradeTaskStatus(taskId));
    }

    /**
     * 获取批改完成后的班级分析报告
     */
    @ApiOperation("获取习题班级分析报告")
    @GetMapping("/chapters/{chapterId}/exercises/{exerciseId}/report")
    public Result<ExerciseClassReportVO> getExerciseReport(@PathVariable Long courseId,
                                                           @PathVariable Long chapterId,
                                                           @PathVariable Long exerciseId) {
        return Result.ok(exerciseService.getClassReport(exerciseId));
    }
}