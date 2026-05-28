package kdec.apple.cloud.app.common.dto.materials;

import kdec.apple.cloud.app.entity.enums.FileType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import kdec.apple.cloud.app.entity.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MaterialDetailVO {
    private Long materialId;
    private String title;
    private String fileName;
    private FileType fileType;
    private String ossUrl;
    private Long chapterId;
    private UserRole uploaderRole;
    private ParseStatus parseStatus;
    private LocalDateTime createdAt;
}