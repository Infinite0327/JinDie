package kdec.apple.cloud.app.business.service;

import kdec.apple.cloud.app.common.entity.ExerciseItem;

import java.util.List;

public interface AiService {
    String generateChapterMindMap(Long chapterId, String chapterTitle, String chapterContent);

    String generateChapterExercises(Long chapterId, String chapterTitle, String chapterContent, Integer questionCount);

    void generateGraphAsync(Long taskId, String fileUrl, Long materialId);

    void generateNoteAsync(Long taskId, String fileUrl, Long materialId);

    void parseAudioAsync(Long taskId, String fileUrl, Long materialId);

    void parseVideoAsync(Long taskId, String fileUrl, Long materialId);

    void parseExerciseAsync(Long taskId, String fileUrl, Long batchId);

    List<ExerciseItem> getExercisePreview(Long taskId);

    String getAnswerPreview(Long taskId);

    void gradeAsync(Long taskId, Long exerciseId);

    String getClassReport(Long exerciseId);
}
