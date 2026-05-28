package kdec.apple.cloud.app.business.service.teacher;

import kdec.apple.cloud.app.common.dto.chapter.ChapterDTO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterSortDTO;

import java.util.List;

public interface TeacherChapterService {
    ChapterDetailVO createChapter(ChapterDTO dto);

    void updateChapter(Long chapterId, ChapterDTO dto);

    void deleteChapter(Long chapterId);

    List<ChapterDetailVO> listChapters(Long courseId);

    void sortChapters(Long courseId, ChapterSortDTO dto);
}
