package kdec.apple.cloud.app.common.dto.materials;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoteVO {
    private Long noteId;
    private Long materialId;
    private String title;
    /** Markdown格式正文 */
    private String content;
    private LocalDateTime updatedAt;
}
