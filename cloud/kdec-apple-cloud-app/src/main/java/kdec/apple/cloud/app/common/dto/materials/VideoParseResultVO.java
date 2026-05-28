package kdec.apple.cloud.app.common.dto.materials;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VideoParseResultVO {
    private Long materialId;
    /** 字幕/转录 */
    private String transcript;
    /** 视频章节摘要 */
    private List<VideoChapter> chapters;
    /** 关键帧关键词 */
    private List<String> keywords;
    /** 视频总时长（秒） */
    private Integer duration;

    @Data
    public static class VideoChapter {
        private Double startTime;
        private Double endTime;
        private String title;
        private String summary;
        private String thumbnailUrl;
    }
}
