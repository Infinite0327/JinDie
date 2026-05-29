package kdec.apple.cloud.app.common.dto.exercise;

public class ExerciseItemSearchRequest {
    private Long batchId;
    private String keyword;
    private String type;

    public ExerciseItemSearchRequest() {
    }

    public Long getBatchId() {
        return batchId;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getType() {
        return type;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setType(String type) {
        this.type = type;
    }
}