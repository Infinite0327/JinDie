package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WrongAnswerDistributionVO {
    private List<ChapterWrongDistribution> chapters;

    @Data
    public static class ChapterWrongDistribution {
        private Long chapterId;
        private String chapterTitle;
        // key: 题型, value: 错误次数
        private Map<String, Integer> byExerciseType;
        // 该章节总错题数
        private Integer totalWrongCount;
    }
}