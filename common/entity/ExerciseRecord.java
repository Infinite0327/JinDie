package kdec.apple.cloud.app.common.entity;

import java.time.LocalDateTime;

public class ExerciseRecord {
    private Long id;
    private Long studentId;
    private Long batchId;
    private Long exerciseItemId;
    private String studentAnswer;
    private Boolean correct;
    private Integer score;
    private String aiFeedback;
    private LocalDateTime submitTime;

    public ExerciseRecord() {
    }

    public ExerciseRecord(Long id, Long studentId, Long batchId, Long exerciseItemId,
                          String studentAnswer, Boolean correct, Integer score,
                          String aiFeedback, LocalDateTime submitTime) {
        this.id = id;
        this.studentId = studentId;
        this.batchId = batchId;
        this.exerciseItemId = exerciseItemId;
        this.studentAnswer = studentAnswer;
        this.correct = correct;
        this.score = score;
        this.aiFeedback = aiFeedback;
        this.submitTime = submitTime;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public Long getExerciseItemId() {
        return exerciseItemId;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public Integer getScore() {
        return score;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public void setExerciseItemId(Long exerciseItemId) {
        this.exerciseItemId = exerciseItemId;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }
}