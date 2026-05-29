package kdec.apple.cloud.app.common.dto.exercise;

public class SubmitExerciseItemResponse {
    private Boolean correct;
    private String rightAnswer;
    private String explanation;
    private Integer score;
    private String aiFeedback;

    public SubmitExerciseItemResponse() {
    }

    public SubmitExerciseItemResponse(Boolean correct, String rightAnswer,
                                      String explanation, Integer score, String aiFeedback) {
        this.correct = correct;
        this.rightAnswer = rightAnswer;
        this.explanation = explanation;
        this.score = score;
        this.aiFeedback = aiFeedback;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public Integer getScore() {
        return score;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }
}