package kdec.apple.cloud.app.business.service.teacher;

import kdec.apple.cloud.app.common.dto.teacherClassManagement.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherClassService {

    //班级

    ClassVO createClass(ClassDTO dto);

    void updateClass(ClassDTO dto);

    void deleteClass(Long classId);

    List<ClassVO> getClassList();

    ClassVO getClassDetail(Long classId);

    //学生

    void addStudent(Long classId, StudentDTO dto);

    StudentBatchAddResultVO batchAddStudents(Long classId, MultipartFile file);

    void removeStudent(Long classId, Long studentId);

    List<StudentVO> listStudents(Long classId);
}
