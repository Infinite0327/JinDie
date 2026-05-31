package kdec.apple.cloud.app.common.dto.utils;

import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreatedVO {
    private Long taskId;
    private Long bizId;// 根据taskType不同含义不同：
    // GRAPH/SUMMARY/AUDIO/VIDEO → materialId
    // GRADE → exerciseId
    // TEACHING_SUGGESTION → classId
    private TaskType taskType;
    /** 预估完成时间（秒） */
    private Integer estimatedSeconds;
}
