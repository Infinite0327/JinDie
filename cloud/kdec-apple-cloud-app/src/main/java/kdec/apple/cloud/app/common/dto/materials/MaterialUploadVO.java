package kdec.apple.cloud.app.common.dto.materials;

import kdec.apple.cloud.app.entity.enums.FileType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 上传状态返回(任务创建瞬间)
 */
@Data
@Builder
public class MaterialUploadVO {
    private Long materialId;
    private String ossUrl;
    private String fileName;
    private FileType fileType;      // String改成枚举
    private LocalDateTime createdAt;
}
