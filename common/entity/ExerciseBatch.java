package kdec.apple.cloud.app.common.entity;

import kdec.apple.cloud.app.common.enums.ParseStatus;

import java.time.LocalDateTime;

public class ExerciseBatch {
    private Long id;
    private Long chapterId;
    private Long taskId;
    private ParseStatus status;
    private String ossUrl;
    private Integer totalCount;
    private LocalDateTime deadline;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public ExerciseBatch() {
    }

    public ExerciseBatch(Long id, Long chapterId, Long taskId, ParseStatus status,
                         String ossUrl, Integer totalCount, LocalDateTime deadline,
                         LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.chapterId = chapterId;
        this.taskId = taskId;
        this.status = status;
        this.ossUrl = ossUrl;
        this.totalCount = totalCount;
        this.deadline = deadline;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public ParseStatus getStatus() {
        return status;
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setStatus(ParseStatus status) {
        this.status = status;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}