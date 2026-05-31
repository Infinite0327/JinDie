package kdec.apple.cloud.app.common.dto.utils;

import kdec.apple.cloud.app.common.enums.ParseStatus;

public class TaskStatusVO {
    private Long taskId;
    private Long bizId;
    private String taskType;
    private ParseStatus status;
    private String failReason;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public ParseStatus getStatus() {
        return status;
    }

    public void setStatus(ParseStatus status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
