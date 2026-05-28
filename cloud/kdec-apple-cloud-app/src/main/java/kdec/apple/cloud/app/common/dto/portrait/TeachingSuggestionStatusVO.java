package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

@Data
public class TeachingSuggestionStatusVO {
    private Long taskId;
    private String status; // PENDING / PROCESSING / DONE / FAILED
    private String failReason;
}
