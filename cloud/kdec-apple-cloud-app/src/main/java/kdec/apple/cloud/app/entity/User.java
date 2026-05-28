package kdec.apple.cloud.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import kdec.apple.cloud.app.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String email;
    private String name;        // 真实姓名
    private UserRole role;        // TEACHER / STUDENT
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}