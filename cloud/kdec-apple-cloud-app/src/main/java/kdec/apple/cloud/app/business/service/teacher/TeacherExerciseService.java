package kdec.apple.cloud.app.business.service.teacher;

import kdec.apple.cloud.app.common.dto.answer.AnswerBatchResultVO;
import kdec.apple.cloud.app.common.dto.answer.AnswerConfirmDTO;
import kdec.apple.cloud.app.common.dto.answer.AnswerItem;
import kdec.apple.cloud.app.common.dto.exercise.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.entity.ExerciseBatch;
import kdec.apple.cloud.app.entity.ExerciseItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherExerciseService {
    TaskCreatedVO upload(ExerciseUploadDTO dto, MultipartFile file);

    List<ExerciseItem> getPreview(Long taskId);

    ExerciseBatchResultVO confirm(ExerciseConfirmDTO dto);

    void update(Long exerciseId, ExerciseItem item);

    void deleteBatch(Long batchId);

    List<ExerciseItem> listByBatch(Long chapterId, Long batchId);

    TaskCreatedVO uploadAnswer(Long classId, Long chapterId, MultipartFile file);

    AnswerItem getAnswerPreview(Long taskId);

    AnswerBatchResultVO confirmAnswer(AnswerConfirmDTO dto);

    void updateAnswer(Long itemId, AnswerItem item);

    void deleteAnswer(Long batchId);

    TaskCreatedVO triggerAutoGrade(Long exerciseId);

    GradeTaskStatusVO getGradeTaskStatus(Long taskId);

    ExerciseClassReportVO getClassReport(Long exerciseId);

    void deleteExercise(Long itemId);

    List<ExerciseBatch> listBatchByChapter(Long chapterId);
}
