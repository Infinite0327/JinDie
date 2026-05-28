package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("student")
public class Student {
    @TableId
    private Long userId;        // 直接用userId做主键
    private String studentNo;   // 学号，学生独有
    private LocalDateTime createTime;
}