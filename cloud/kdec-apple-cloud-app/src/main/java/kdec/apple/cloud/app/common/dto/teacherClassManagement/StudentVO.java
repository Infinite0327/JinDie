package kdec.apple.cloud.app.common.dto.teacherClassManagement;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentVO {
    private Long studentId;
    private String studentNo;
    private String name;
    private String email;
    private LocalDateTime joinedAt;
}
