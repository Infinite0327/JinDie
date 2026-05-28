package kdec.apple.cloud.app.common.dto.materials;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AudioParseResultVO {
    private Long materialId;
    /** 完整转录文本 */
    private String transcript;
    /** 带时间戳的分段 */
    private List<AudioSegment> segments;
    /** 关键词列表 */
    private List<String> keywords;

    @Data
    public static class AudioSegment {
        private Double startTime;
        private Double endTime;
        private String text;
        /** 说话人标识 */
        private String speaker;
    }
}
