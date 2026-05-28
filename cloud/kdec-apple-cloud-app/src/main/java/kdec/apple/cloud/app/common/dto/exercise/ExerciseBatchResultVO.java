package kdec.apple.cloud.app.common.dto.exercise;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseBatchResultVO {
    private Long batchId;
    private Integer successCount;
    private Integer failCount;
}
