package kdec.apple.cloud.app.common.dto.answer;

import kdec.apple.cloud.app.entity.ExerciseItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnswerConfirmDTO {
    private Long taskId;
    private Long answerBatchId;
    private LocalDateTime deadline;  // 统一设置截止时间
    private List<AnswerItem> answers;
}