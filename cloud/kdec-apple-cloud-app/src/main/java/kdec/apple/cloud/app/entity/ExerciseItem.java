package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.common.dto.enums.ExerciseType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
//TODO
@Data
@Builder
@TableName("exercise_item")
public class ExerciseItem {
    @TableId
    private Long id;
    private Long batchId;//属于那一批上传
    private ExerciseType type;          // SINGLE/MULTIPLE/JUDGE/FILL
    private String content;
    private String options;       // JSON
    private String answer;        // JSON数组
    private String explanation;
    private String knowledgeTags; // JSON数组，原keyword改名
}