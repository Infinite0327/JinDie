package kdec.apple.cloud.app.webapi.teacher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kdec.apple.base.common.result.Result;
import kdec.apple.cloud.app.business.service.MaterialService;
import kdec.apple.cloud.app.business.service.TaskQueryService;
import kdec.apple.cloud.app.business.service.teacher.TeacherChapterService;
import kdec.apple.cloud.app.business.service.teacher.TeacherExerciseService;
import kdec.apple.cloud.app.common.dto.answer.AnswerBatchResultVO;
import kdec.apple.cloud.app.common.dto.answer.AnswerConfirmDTO;
import kdec.apple.cloud.app.common.dto.answer.AnswerItem;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDTO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterSortDTO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseBatchResultVO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseClassReportVO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseConfirmDTO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseUploadDTO;
import kdec.apple.cloud.app.common.dto.exercise.GradeTaskStatusVO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUpdateDTO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUploadDTO;
import kdec.apple.cloud.app.common.dto.materials.MaterialUploadVO;
import kdec.apple.cloud.app.common.dto.materials.MaterialVO;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import kdec.apple.cloud.app.entity.ExerciseBatch;
import kdec.apple.cloud.app.entity.ExerciseItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "Teacher course management")
@RestController
@RequestMapping("/teacher/courses/{classId}")
@RequiredArgsConstructor
public class TeacherCourseController {
    private final TeacherExerciseService exerciseService;
    private final MaterialService materialService;
    private final TeacherChapterService chapterService;
    private final TaskQueryService taskQueryService;

    @PostMapping("/chapters")
    public Result<ChapterDetailVO> createChapter(@PathVariable Long classId, @RequestBody ChapterDTO dto) {
        dto.setCourseId(classId);
        return Result.ok(chapterService.createChapter(dto));
    }

    @PutMapping("/chapters/{chapterId}")
    public Result<Void> updateChapter(@PathVariable Long chapterId, @RequestBody ChapterDTO dto) {
        chapterService.updateChapter(chapterId, dto);
        return Result.ok();
    }

    @DeleteMapping("/chapters/{chapterId}")
    public Result<Void> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return Result.ok();
    }

    @GetMapping("/chapters")
    public Result<List<ChapterDetailVO>> listChapters(@PathVariable Long classId) {
        return Result.ok(chapterService.listChapters(classId));
    }

    @PutMapping("/chapters/sort")
    public Result<Void> sortChapters(@PathVariable Long classId, @RequestBody ChapterSortDTO dto) {
        chapterService.sortChapters(classId, dto);
        return Result.ok();
    }

    @PostMapping("/chapters/{chapterId}/materials")
    public Result<MaterialUploadVO> uploadMaterial(@PathVariable Long classId, @PathVariable Long chapterId,
                                                   @RequestPart("file") MultipartFile file,
                                                   @ModelAttribute MaterialUploadDTO dto) {
        dto.setCourseId(classId);
        dto.setChapterId(chapterId);
        return Result.ok(materialService.upload(file, dto));
    }

    @DeleteMapping("/chapters/{chapterId}/materials/{materialId}")
    public Result<Void> deleteMaterial(@PathVariable Long materialId) {
        materialService.delete(materialId);
        return Result.ok();
    }

    @PutMapping("/chapters/{chapterId}/materials/{materialId}")
    public Result<Void> updateMaterial(@PathVariable Long materialId, @RequestBody MaterialUpdateDTO dto) {
        materialService.update(materialId, dto);
        return Result.ok();
    }

    @GetMapping("/chapters/{chapterId}/materials")
    public Result<List<MaterialVO>> listMaterials(@PathVariable Long classId, @PathVariable Long chapterId) {
        return Result.ok(materialService.listByChapter(classId, chapterId));
    }

    @GetMapping("/tasks/{taskId}/status")
    public Result<TaskStatusVO> getTaskStatus(@PathVariable Long taskId) {
        return Result.ok(taskQueryService.getTaskStatus(taskId));
    }

    @PostMapping("/chapters/{chapterId}/exercises")
    public Result<TaskCreatedVO> uploadExercises(@PathVariable Long chapterId, @ModelAttribute ExerciseUploadDTO dto,
                                                 @RequestPart("file") MultipartFile file) {
        dto.setChapterId(chapterId);
        return Result.ok(exerciseService.upload(dto, file));
    }

    @GetMapping("/chapters/{chapterId}/exercises/preview/{taskId}")
    public Result<List<ExerciseItem>> getExercisePreview(@PathVariable Long taskId) {
        return Result.ok(exerciseService.getPreview(taskId));
    }

    @PostMapping("/chapters/{chapterId}/exercises/confirm")
    public Result<ExerciseBatchResultVO> confirmExercises(@RequestBody ExerciseConfirmDTO dto) {
        return Result.ok(exerciseService.confirm(dto));
    }

    @PutMapping("/chapters/{chapterId}/exercises/{exerciseId}")
    public Result<Void> updateExercise(@PathVariable Long exerciseId, @RequestBody ExerciseItem item) {
        exerciseService.update(exerciseId, item);
        return Result.ok();
    }

    @DeleteMapping("/chapters/{chapterId}/exercises/batches/{batchId}")
    public Result<Void> deleteExerciseBatch(@PathVariable Long batchId) {
        exerciseService.deleteBatch(batchId);
        return Result.ok();
    }

    @DeleteMapping("/chapters/{chapterId}/exercises/{itemId}")
    public Result<Void> deleteExerciseItem(@PathVariable Long itemId) {
        exerciseService.deleteExercise(itemId);
        return Result.ok();
    }

    @GetMapping("/chapters/{chapterId}/exercises/batches")
    public Result<List<ExerciseBatch>> listBatches(@PathVariable Long chapterId) {
        return Result.ok(exerciseService.listBatchByChapter(chapterId));
    }

    @GetMapping("/chapters/{chapterId}/exercises/batches/{batchId}")
    public Result<List<ExerciseItem>> listExercises(@PathVariable Long chapterId, @PathVariable Long batchId) {
        return Result.ok(exerciseService.listByBatch(chapterId, batchId));
    }

    @PostMapping("/chapters/{chapterId}/answers/upload")
    public Result<TaskCreatedVO> uploadAnswer(@PathVariable Long classId, @PathVariable Long chapterId,
                                              @RequestPart("file") MultipartFile file) {
        return Result.ok(exerciseService.uploadAnswer(classId, chapterId, file));
    }

    @GetMapping("/chapters/{chapterId}/answers/preview/{taskId}")
    public Result<AnswerItem> getAnswerPreview(@PathVariable Long taskId) {
        return Result.ok(exerciseService.getAnswerPreview(taskId));
    }

    @PostMapping("/chapters/{chapterId}/answers/confirm")
    public Result<AnswerBatchResultVO> confirmAnswer(@RequestBody AnswerConfirmDTO dto) {
        return Result.ok(exerciseService.confirmAnswer(dto));
    }

    @PutMapping("/chapters/{chapterId}/answers/{itemId}")
    public Result<Void> updateAnswer(@PathVariable Long itemId, @RequestBody AnswerItem item) {
        exerciseService.updateAnswer(itemId, item);
        return Result.ok();
    }

    @DeleteMapping("/chapters/{chapterId}/answers/batches/{answerBatchId}")
    public Result<Void> deleteAnswer(@PathVariable Long answerBatchId) {
        exerciseService.deleteAnswer(answerBatchId);
        return Result.ok();
    }

    @PostMapping("/chapters/{chapterId}/exercises/{exerciseId}/grade")
    public Result<TaskCreatedVO> triggerAutoGrade(@PathVariable Long exerciseId) {
        return Result.ok(exerciseService.triggerAutoGrade(exerciseId));
    }

    @GetMapping("/grade-tasks/{taskId}/status")
    public Result<GradeTaskStatusVO> getGradeTaskStatus(@PathVariable Long taskId) {
        return Result.ok(exerciseService.getGradeTaskStatus(taskId));
    }

    @GetMapping("/chapters/{chapterId}/exercises/{exerciseId}/report")
    public Result<ExerciseClassReportVO> getExerciseReport(@PathVariable Long exerciseId) {
        return Result.ok(exerciseService.getClassReport(exerciseId));
    }
}
