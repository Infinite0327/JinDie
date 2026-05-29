package kdec.apple.cloud.app.common.dto.exercise;

public class ExerciseItemViewDTO {
    private Long exerciseItemId;
    private Long batchId;
    private String type;
    private String content;
    private String options;
    private String knowledgeTags;

    public ExerciseItemViewDTO() {
    }

    public ExerciseItemViewDTO(Long exerciseItemId, Long batchId, String type,
                               String content, String options, String knowledgeTags) {
        this.exerciseItemId = exerciseItemId;
        this.batchId = batchId;
        this.type = type;
        this.content = content;
        this.options = options;
        this.knowledgeTags = knowledgeTags;
    }

    public Long getExerciseItemId() {
        return exerciseItemId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getOptions() {
        return options;
    }

    public String getKnowledgeTags() {
        return knowledgeTags;
    }

    public void setExerciseItemId(Long exerciseItemId) {
        this.exerciseItemId = exerciseItemId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setKnowledgeTags(String knowledgeTags) {
        this.knowledgeTags = knowledgeTags;
    }
}