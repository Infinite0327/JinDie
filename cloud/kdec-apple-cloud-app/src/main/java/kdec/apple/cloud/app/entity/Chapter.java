package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("chapter")
public class Chapter {
    @TableId
    private Long Id;
    private Long classId;
    private String title;
    private Integer sortIndex;
    private boolean finished;
    private LocalDateTime createTime;
}
