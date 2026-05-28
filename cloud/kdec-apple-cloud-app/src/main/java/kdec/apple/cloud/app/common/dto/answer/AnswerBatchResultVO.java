package kdec.apple.cloud.app.common.dto.answer;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerBatchResultVO {
    private Long batchId;
    private Integer successCount;
    private Integer failCount;
}
