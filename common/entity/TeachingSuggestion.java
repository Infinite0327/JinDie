package kdec.apple.cloud.app.common.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeachingSuggestion {
    private Long classId;
    private String content;
    private List<Long> focusStudentIds = new ArrayList<>();
    private List<String> suggestedKnowledgeTags = new ArrayList<>();
    private LocalDateTime createTime;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Long> getFocusStudentIds() {
        return focusStudentIds;
    }

    public void setFocusStudentIds(List<Long> focusStudentIds) {
        this.focusStudentIds = focusStudentIds;
    }

    public List<String> getSuggestedKnowledgeTags() {
        return suggestedKnowledgeTags;
    }

    public void setSuggestedKnowledgeTags(List<String> suggestedKnowledgeTags) {
        this.suggestedKnowledgeTags = suggestedKnowledgeTags;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
