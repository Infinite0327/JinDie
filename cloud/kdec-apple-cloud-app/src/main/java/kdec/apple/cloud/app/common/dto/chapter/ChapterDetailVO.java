package kdec.apple.cloud.app.common.dto.chapter;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChapterDetailVO {
    private Long chapterId;
    private Long courseId;
    private String title;
    private String description;
    private Integer sortIndex;
    private Integer materialCount;
    private Integer exerciseCount;
    private LocalDateTime createTime;
}
