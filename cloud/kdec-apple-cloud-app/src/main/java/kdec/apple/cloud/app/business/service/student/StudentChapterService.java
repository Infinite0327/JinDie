package kdec.apple.cloud.app.business.service.student;


import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterListVO;

import java.util.List;

public interface StudentChapterService {
    List<ChapterListVO> listChapters(Long courseId);

    ChapterDetailVO getChapterDetail(Long courseId, Long chapterId);
}
