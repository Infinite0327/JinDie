package kdec.apple.cloud.app.common.dto.teacherClassManagement;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class StudentBatchAddResultVO {
    private Integer successCount;
    private Integer failCount;
    // 失败原因列表
    private List<String> failReasons;
}
