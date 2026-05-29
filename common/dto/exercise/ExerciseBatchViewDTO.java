package kdec.apple.cloud.app.common.dto.exercise;

import kdec.apple.cloud.app.common.enums.ParseStatus;

import java.time.LocalDateTime;

public class ExerciseBatchViewDTO {
    private Long batchId;
    private Long chapterId;
    private ParseStatus status;
    private Integer totalCount;
    private LocalDateTime deadline;

    public ExerciseBatchViewDTO() {
    }

    public ExerciseBatchViewDTO(Long batchId, Long chapterId, ParseStatus status,
                                Integer totalCount, LocalDateTime deadline) {
        this.batchId = batchId;
        this.chapterId = chapterId;
        this.status = status;
        this.totalCount = totalCount;
        this.deadline = deadline;
    }

    public Long getBatchId() {
        return batchId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public ParseStatus getStatus() {
        return status;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public void setStatus(ParseStatus status) {
        this.status = status;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}