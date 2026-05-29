package kdec.apple.cloud.app.common.entity;

import kdec.apple.cloud.app.common.enums.ExerciseType;

public class ExerciseItem {
    private Long id;
    private Long batchId;
    private ExerciseType type;
    private String content;
    private String options;
    private String answer;
    private String explanation;
    private String knowledgeTags;

    public ExerciseItem() {
    }

    public ExerciseItem(Long id, Long batchId, ExerciseType type,
                        String content, String options, String answer,
                        String explanation, String knowledgeTags) {
        this.id = id;
        this.batchId = batchId;
        this.type = type;
        this.content = content;
        this.options = options;
        this.answer = answer;
        this.explanation = explanation;
        this.knowledgeTags = knowledgeTags;
    }

    public Long getId() {
        return id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public ExerciseType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getKnowledgeTags() {
        return knowledgeTags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setKnowledgeTags(String knowledgeTags) {
        this.knowledgeTags = knowledgeTags;
    }
}