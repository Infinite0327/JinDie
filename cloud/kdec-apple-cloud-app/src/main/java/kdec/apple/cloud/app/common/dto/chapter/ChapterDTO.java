package kdec.apple.cloud.app.common.dto.chapter;

import lombok.Data;

@Data
public class ChapterDTO {
    private Long courseId;
    private String title;
    private String description;
    private Integer sortIndex;  // 排序序号
}
