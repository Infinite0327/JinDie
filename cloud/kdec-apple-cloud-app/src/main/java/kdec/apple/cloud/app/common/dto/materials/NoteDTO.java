package kdec.apple.cloud.app.common.dto.materials;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDTO {
    private Long materialId;
    /** 笔记正文（富文本/Markdown） */
    private String content;
    /** 笔记标题 */
    private String title;
}