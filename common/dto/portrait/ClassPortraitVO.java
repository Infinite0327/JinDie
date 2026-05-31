package kdec.apple.cloud.app.common.dto.portrait;

public class ClassPortraitVO {
    private Long classId;
    private String className;
    private Integer studentCount;
    private Double avgStudyMinutesPerDay;
    private Double avgAccuracy;
    private Double avgExerciseCompletion;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Double getAvgStudyMinutesPerDay() {
        return avgStudyMinutesPerDay;
    }

    public void setAvgStudyMinutesPerDay(Double avgStudyMinutesPerDay) {
        this.avgStudyMinutesPerDay = avgStudyMinutesPerDay;
    }

    public Double getAvgAccuracy() {
        return avgAccuracy;
    }

    public void setAvgAccuracy(Double avgAccuracy) {
        this.avgAccuracy = avgAccuracy;
    }

    public Double getAvgExerciseCompletion() {
        return avgExerciseCompletion;
    }

    public void setAvgExerciseCompletion(Double avgExerciseCompletion) {
        this.avgExerciseCompletion = avgExerciseCompletion;
    }
}
