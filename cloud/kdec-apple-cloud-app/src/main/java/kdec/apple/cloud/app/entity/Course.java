package kdec.apple.cloud.app.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.entity.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course")
public class Course {
    @TableId
    private Long id;
    private Long teacherId;
    private String name;
    private String description;
    private CourseStatus status;  // DRAFT / PUBLISHED / CLOSED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}