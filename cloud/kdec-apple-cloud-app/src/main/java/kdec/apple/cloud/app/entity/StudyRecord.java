package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("study_record")
public class StudyRecord {
    @TableId
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long chapterId;
    private Integer durationMinutes;
    private LocalDate studyDate;
    private LocalDateTime createTime;
}
