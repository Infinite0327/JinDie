package kdec.apple.cloud.app.common.dto.chapter;

import lombok.Data;

@Data
public class ChapterListVO {
    private Long chapterId;
    private String title;
    private Integer sortIndex;
}
