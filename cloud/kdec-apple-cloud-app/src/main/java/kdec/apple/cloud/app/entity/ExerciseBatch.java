package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("exercise_batch")
public class ExerciseBatch {
    @TableId
    private Long id;
    private Long chapterId;
    private Long taskId;
    private ParseStatus parseStatus;
    private String ossUrl;
    private Integer totalCount;
    private LocalDateTime deadline;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
