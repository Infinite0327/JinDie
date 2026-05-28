package kdec.apple.cloud.app.common.dto.exercise;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExerciseUploadDTO {
    private Long chapterId;
    private String title;
    private LocalDateTime deadline;  // 截止时间在上传时设置
}
