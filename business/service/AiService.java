package kdec.apple.cloud.app.business.service;

public interface AiService {
    String generateChapterMindMap(Long chapterId, String chapterTitle, String chapterContent);

    String generateChapterExercises(Long chapterId, String chapterTitle, String chapterContent, Integer questionCount);
}
