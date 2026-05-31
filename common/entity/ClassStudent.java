package kdec.apple.cloud.app.common.entity;

public class ClassStudent {
    private Long classId;
    private Long studentId;
    private String studentName;

    public ClassStudent() {
    }

    public ClassStudent(Long classId, Long studentId, String studentName) {
        this.classId = classId;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public Long getClassId() {
        return classId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }
}
