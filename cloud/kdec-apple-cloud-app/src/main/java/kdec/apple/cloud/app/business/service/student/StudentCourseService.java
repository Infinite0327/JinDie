package kdec.apple.cloud.app.business.service.student;

import kdec.apple.cloud.app.common.dto.studentCourse.CourseDetailVO;
import kdec.apple.cloud.app.common.dto.studentCourse.CourseVO;

import java.util.List;

public interface StudentCourseService {
    List<CourseVO> listMyCourses(Long studentId);

    CourseDetailVO getCourseDetail(Long courseId);
}
