package kdec.apple.cloud.app.common.dto.answer;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("answer_item")
public class AnswerItem {
    private Long id;
    private Long answerBatchId;   // 对应AnswerBatch.id
    private Long exerciseItemId;  // 对应ExerciseItem.id
    private List<String> answer;  // JSON
    private String explanation;
}