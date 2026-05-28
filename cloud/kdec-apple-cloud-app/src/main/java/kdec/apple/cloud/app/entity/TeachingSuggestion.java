package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("teaching_suggestion")
public class TeachingSuggestion {
    private Long id;
    private Long classId;
    // Markdown正文
    private String content;
    // 重点关注学生JSON：[{ studentId, studentName, reason }]
    private String focusStudents;
    // 建议知识点JSON：["知识点1"]
    private String suggestedKnowledgeTags;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}