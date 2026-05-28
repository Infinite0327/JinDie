package kdec.apple.cloud.app.common.dto.answer;

import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("answer_batch")
public class AnswerBatch {
    private Long id;
    private Long exerciseBatchId;        // 对应ExerciseBatch.id
    private Long taskId;
    private String ossUrl;
    private ParseStatus parseStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}