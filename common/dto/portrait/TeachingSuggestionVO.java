package kdec.apple.cloud.app.common.dto.portrait;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeachingSuggestionVO {
    private Long classId;
    private String content;
    private List<FocusStudentVO> focusStudents = new ArrayList<>();
    private List<String> suggestedKnowledgeTags = new ArrayList<>();
    private LocalDateTime generatedAt;

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

    public List<FocusStudentVO> getFocusStudents() {
        return focusStudents;
    }

    public void setFocusStudents(List<FocusStudentVO> focusStudents) {
        this.focusStudents = focusStudents;
    }

    public List<String> getSuggestedKnowledgeTags() {
        return suggestedKnowledgeTags;
    }

    public void setSuggestedKnowledgeTags(List<String> suggestedKnowledgeTags) {
        this.suggestedKnowledgeTags = suggestedKnowledgeTags;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public static class FocusStudentVO {
        private Long studentId;
        private String studentName;
        private String reason;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
