package kdec.apple.cloud.app.common.dto.exercise;

import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Data;

@Data
public class GradeTaskStatusVO {
    private Long taskId;
    // PENDING / PROCESSING / DONE / FAILED
    private ParseStatus status;
    // 已批改人数
    private Integer gradedCount;
    // 总人数
    private Integer totalCount;
    private String failReason;
}