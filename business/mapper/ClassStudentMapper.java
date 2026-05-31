package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.ClassStudent;

import java.util.ArrayList;
import java.util.List;

public class ClassStudentMapper {
    private static final List<ClassStudent> STUDENT_LIST = new ArrayList<>();

    static {
        STUDENT_LIST.add(new ClassStudent(1L, 10001L, "Student 10001"));
        STUDENT_LIST.add(new ClassStudent(1L, 10002L, "Student 10002"));
    }

    public List<ClassStudent> selectByClassId(Long classId) {
        List<ClassStudent> result = new ArrayList<>();
        for (ClassStudent student : STUDENT_LIST) {
            if (classId == null || classId.equals(student.getClassId())) {
                result.add(student);
            }
        }
        return result;
    }

    public ClassStudent selectByClassIdAndStudentId(Long classId, Long studentId) {
        for (ClassStudent student : STUDENT_LIST) {
            if ((classId == null || classId.equals(student.getClassId()))
                    && studentId != null
                    && studentId.equals(student.getStudentId())) {
                return student;
            }
        }
        return null;
    }
}
