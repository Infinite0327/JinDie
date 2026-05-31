package kdec.apple.cloud.app.common.dto.portrait;

import java.util.ArrayList;
import java.util.List;

public class ClassWeakKnowledgeVO {
    private List<ChapterWeakKnowledge> chapters = new ArrayList<>();

    public List<ChapterWeakKnowledge> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterWeakKnowledge> chapters) {
        this.chapters = chapters;
    }

    public static class ChapterWeakKnowledge {
        private Long chapterId;
        private String chapterTitle;
        private List<KnowledgeHeatItem> weakPoints = new ArrayList<>();

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

        public List<KnowledgeHeatItem> getWeakPoints() {
            return weakPoints;
        }

        public void setWeakPoints(List<KnowledgeHeatItem> weakPoints) {
            this.weakPoints = weakPoints;
        }
    }

    public static class KnowledgeHeatItem {
        private String tag;
        private Double avgAccuracy;
        private Integer wrongStudentCount;
        private Integer heatLevel;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Double getAvgAccuracy() {
            return avgAccuracy;
        }

        public void setAvgAccuracy(Double avgAccuracy) {
            this.avgAccuracy = avgAccuracy;
        }

        public Integer getWrongStudentCount() {
            return wrongStudentCount;
        }

        public void setWrongStudentCount(Integer wrongStudentCount) {
            this.wrongStudentCount = wrongStudentCount;
        }

        public Integer getHeatLevel() {
            return heatLevel;
        }

        public void setHeatLevel(Integer heatLevel) {
            this.heatLevel = heatLevel;
        }
    }
}
