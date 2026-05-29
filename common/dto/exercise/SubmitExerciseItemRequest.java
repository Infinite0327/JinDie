package kdec.apple.cloud.app.common.dto.exercise;

public class SubmitExerciseItemRequest {
    private Long studentId;
    private Long exerciseItemId;
    private String studentAnswer;

    public SubmitExerciseItemRequest() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getExerciseItemId() {
        return exerciseItemId;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setExerciseItemId(Long exerciseItemId) {
        this.exerciseItemId = exerciseItemId;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }
}