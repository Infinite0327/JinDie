package kdec.apple.cloud.app.common.dto.utils;

public class TaskCreatedVO {
    private Long taskId;
    private Long bizId;
    private String taskType;
    private Integer estimatedSeconds;

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

    public Integer getEstimatedSeconds() {
        return estimatedSeconds;
    }

    public void setEstimatedSeconds(Integer estimatedSeconds) {
        this.estimatedSeconds = estimatedSeconds;
    }
}
