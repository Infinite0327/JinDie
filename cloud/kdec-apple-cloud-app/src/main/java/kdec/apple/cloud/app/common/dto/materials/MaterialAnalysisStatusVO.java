package kdec.apple.cloud.app.common.dto.materials;


import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Builder;
import lombok.Data;

/**
 * 轮询状态 + 解析结果(任务执行过程)
 * */
@Data
@Builder
public class MaterialAnalysisStatusVO {


        private Long taskId;

        private TaskType taskType;          // GRAPH | NOTE | AUDIO | VIDEO

        private ParseStatus parseStatus;  // PENDING | PROCESSING | DONE | FAILED

        private String failReason;


}
