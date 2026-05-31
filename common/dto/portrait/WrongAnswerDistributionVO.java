package kdec.apple.cloud.app.common.dto.portrait;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WrongAnswerDistributionVO {
    private List<ChapterWrongDistribution> chapters = new ArrayList<>();

    public List<ChapterWrongDistribution> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterWrongDistribution> chapters) {
        this.chapters = chapters;
    }

    public static class ChapterWrongDistribution {
        private Long chapterId;
        private String chapterTitle;
        private Map<String, Integer> byExerciseType = new LinkedHashMap<>();
        private Integer totalWrongCount;

        public Long getChapterId() {
            return chapterId;
        }

        public void setChapterId(Long chapterId) {
            this.chapterId = chapterId;
        }

        public String getChapterTitle() {
            return chapterTitle;
        }

        public void setChapterTitle(String chapterTitle) {
            this.chapterTitle = chapterTitle;
        }

        public Map<String, Integer> getByExerciseType() {
            return byExerciseType;
        }

        public void setByExerciseType(Map<String, Integer> byExerciseType) {
            this.byExerciseType = byExerciseType;
        }

        public Integer getTotalWrongCount() {
            return totalWrongCount;
        }

        public void setTotalWrongCount(Integer totalWrongCount) {
            this.totalWrongCount = totalWrongCount;
        }
    }
}
