package kdec.apple.cloud.app.business.service.teacher;

import kdec.apple.cloud.app.common.dto.teacherClassManagement.StudentDTO;
import kdec.apple.cloud.app.common.dto.teacherClassManagement.StudentPageQueryDTO;
import kdec.apple.cloud.app.common.dto.utils.PageResult;

public interface StudentService {
    void updateStudent(Long classId, Long studentId, StudentDTO dto) ;

    PageResult pageQuery(StudentPageQueryDTO queryDTO) ;
}
