package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("announcement")
public class Announcement {
    @TableId
    private Long id;
    private Long classId;
    private Long teacherId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
