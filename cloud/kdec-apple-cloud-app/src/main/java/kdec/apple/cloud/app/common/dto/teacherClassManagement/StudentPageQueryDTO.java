package kdec.apple.cloud.app.common.dto.teacherClassManagement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentPageQueryDTO {
    private Long classId;
    private String keyword;  // 模糊搜索姓名或学号
    private Integer page = 1;
    private Integer pageSize = 20;
}
