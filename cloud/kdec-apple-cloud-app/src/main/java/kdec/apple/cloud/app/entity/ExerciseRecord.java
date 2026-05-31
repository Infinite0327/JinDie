package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("exercise_record")
public class ExerciseRecord {
    @TableId
    private Long id;
    private Long studentId;
    private Long batchId;
    private Long exerciseItemId;
    private String studentAnswer;
    private Boolean correct;
    private Integer score;
    private String aiFeedback;
    private LocalDateTime submitTime;
}
