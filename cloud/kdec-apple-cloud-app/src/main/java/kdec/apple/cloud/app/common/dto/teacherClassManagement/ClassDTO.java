package kdec.apple.cloud.app.common.dto.teacherClassManagement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassDTO {
    private String name;        // 班级名称
    private String description; // 班级描述
    private Long classId;
}
