package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调用ai的异步任务
 */
@Data
@Builder
@TableName("task")
public class Task {
    @TableId
    private Long taskId;
    // GRAPH / NOTE / AUDIO / VIDEO / GRADE / TEACHING_SUGGESTION
    private TaskType taskType;
    // PENDING / PROCESSING / DONE / FAILED
    private ParseStatus status;
    // 关联业务ID（materialId / exerciseId / classId）
    private Long bizId;
    private String failReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
