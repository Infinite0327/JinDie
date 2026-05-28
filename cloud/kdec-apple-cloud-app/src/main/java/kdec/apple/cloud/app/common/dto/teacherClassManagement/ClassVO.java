package kdec.apple.cloud.app.common.dto.teacherClassManagement;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class ClassVO {
    private Long classId;
    private String name;
    private String description;
    private Integer studentCount;
    private LocalDateTime createTime;
}
