package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeachingSuggestionVO {
    private Long classId;
    private String content;  // AI生成的教学建议正文（Markdown）

    // 重点关注学生列表
    private List<FocusStudentVO> focusStudents;

    // 建议加强的知识点
    private List<String> suggestedKnowledgeTags;
    private LocalDateTime generatedAt;

    @Data
    public static class FocusStudentVO {
        private Long studentId;
        private String studentName;
        // AI给出的关注原因
        private String reason;
    }
}