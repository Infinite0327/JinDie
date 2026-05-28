package kdec.apple.cloud.app.common.dto.studentCourse;

import lombok.Data;

// 课程列表
@Data
public class CourseVO {
    private Long courseId;
    private String name;
    private String description;
    private String teacherName;
    private Integer chapterCount;
    // 学生该课程整体进度 0-100
    private Integer progress;
}
