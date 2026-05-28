package kdec.apple.cloud.app.common.dto.materials;

import kdec.apple.cloud.app.entity.enums.FileType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
//TODO
/**
 * 资料分页查询
 */
@Data
@Builder
public class MaterialVO {
    private Long materialId;
    private String title;
    private String fileName;
    private FileType fileType;
    private String fileUrl;
    private ParseStatus parseStatus;
    private LocalDateTime createdAt;
}