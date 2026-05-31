package kdec.apple.cloud.app.common.dto.portrait;

import java.util.ArrayList;
import java.util.List;

public class StudentPortraitVO {
    private Long studentId;
    private String studentName;
    private Integer totalStudyMinutes;
    private Integer totalExerciseCount;
    private Double overallAccuracy;
    private Integer continuousStudyDays;
    private List<String> topWeakPoints = new ArrayList<>();

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getTotalStudyMinutes() {
        return totalStudyMinutes;
    }

    public void setTotalStudyMinutes(Integer totalStudyMinutes) {
        this.totalStudyMinutes = totalStudyMinutes;
    }

    public Integer getTotalExerciseCount() {
        return totalExerciseCount;
    }

    public void setTotalExerciseCount(Integer totalExerciseCount) {
        this.totalExerciseCount = totalExerciseCount;
    }

    public Double getOverallAccuracy() {
        return overallAccuracy;
    }

    public void setOverallAccuracy(Double overallAccuracy) {
        this.overallAccuracy = overallAccuracy;
    }

    public Integer getContinuousStudyDays() {
        return continuousStudyDays;
    }

    public void setContinuousStudyDays(Integer continuousStudyDays) {
        this.continuousStudyDays = continuousStudyDays;
    }

    public List<String> getTopWeakPoints() {
        return topWeakPoints;
    }

    public void setTopWeakPoints(List<String> topWeakPoints) {
        this.topWeakPoints = topWeakPoints;
    }
}
