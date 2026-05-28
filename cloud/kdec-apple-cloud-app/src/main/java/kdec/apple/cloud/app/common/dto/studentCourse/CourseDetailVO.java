package kdec.apple.cloud.app.common.dto.studentCourse;

import kdec.apple.cloud.app.common.dto.chapter.ChapterDetailVO;
import kdec.apple.cloud.app.common.dto.chapter.ChapterListVO;
import lombok.Data;

import java.util.List;
@Data
public class CourseDetailVO {
    private Long courseId;
    private String name;
    private String description;
    private String teacherName;
    private Integer chapterCount;
    private Integer progress;
    private List<ChapterListVO> chapters;
}
