package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("note")
public class Note {
    @TableId
    private Long Id;
    private Long materialId;
    private Long studentId;
    private String title;
    // Markdown正文
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

