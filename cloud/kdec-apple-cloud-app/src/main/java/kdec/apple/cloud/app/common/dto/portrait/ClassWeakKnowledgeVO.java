package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.util.List;

@Data
public class ClassWeakKnowledgeVO {
    private List<ChapterWeakKnowledge> chapters;

    @Data
    public static class ChapterWeakKnowledge {
        private Long chapterId;
        private String chapterTitle;
        private List<KnowledgeHeatItem> weakPoints;
    }

    @Data
    public static class KnowledgeHeatItem {
        private String tag;
        // 全班该知识点平均正确率
        private Double avgAccuracy;
        // 错误人数
        private Integer wrongStudentCount;
        // 热力等级 0-4
        private Integer heatLevel;
    }
}
