package kdec.apple.cloud.app.common.dto.exercise;

import kdec.apple.cloud.app.entity.ExerciseItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 上传前确认ai识别出的题
 */
@Data
public class ExerciseConfirmDTO {
    private Long taskId;
    private Long batchId;
    private LocalDateTime deadline;  // 统一设置截止时间
    private List<ExerciseItem> exercises;

}