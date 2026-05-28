package kdec.apple.cloud.app.common;

import lombok.Data;

@Data
public class LoginUser {
    private Long userId;
    private String userName;
    private String role;   // "student" 或 "teacher"

    public boolean isTeacher() {
        return "teacher".equals(this.role);
    }

    public boolean isStudent() {
        return "student".equals(this.role);
    }
}
