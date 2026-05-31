package kdec.apple.cloud.app.business.service;

import kdec.apple.cloud.app.common.dto.answer.AnswerItem;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseClassReportVO;
import kdec.apple.cloud.app.entity.ExerciseItem;

import java.util.List;

public interface AiService {
    void generateGraphAsync(Long taskId, String fileUrl, Long materialId);

    void generateNoteAsync(Long taskId, String fileUrl, Long materialId);

    void parseAudioAsync(Long taskId, String fileUrl, Long materialId);

    void parseVideoAsync(Long taskId, String fileUrl, Long materialId);

    void parseExerciseAsync(Long taskId, String fileUrl, Long batchId);

    void parseAnswerAsync(Long taskId, String fileUrl, Long answerBatchId);

    List<ExerciseItem> getExercisePreview(Long taskId);

    AnswerItem getAnswerPreview(Long taskId);

    void gradeAsync(Long taskId, Long exerciseId);

    ExerciseClassReportVO getClassReport(Long exerciseId);

    String generateTeachingSuggestion(Long classId, String portraitSummary);
}
