package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
//班级-学生关联
//多对多关系表
@Data
@Builder
@TableName("class_student")
public class ClassStudent {
    @TableId
    private Long id;
    private Long classId;
    private Long studentId;
    private LocalDateTime joinedAt;
}