package kdec.apple.cloud.app.common.dto.utils;

import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskStatusVO {
    private Long taskId;
    private Long bizId;
    // 根据taskType不同含义不同：
    // GRAPH/NOTE/AUDIO/VIDEO → materialId
    // EXERCISE_PARSE,  → BatchId
    // ANSWER_PARSE,
    // TEACHING_SUGGESTION → classId
    private TaskType taskType;
    private ParseStatus status;  // PENDING / PROCESSING / DONE / FAILED
    private String failReason;
}