package kdec.apple.cloud.app.common.dto.materials;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传请求
 */
@Data
public class MaterialUploadDTO {
    private Long courseId;
    private Long chapterId;
    private String title;
    private MultipartFile file;
}
