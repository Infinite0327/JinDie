package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.util.List;
//TODO

@Data
public class WeakPointVO {
    private List<WeakKnowledgeTag> weakPoints;

    @Data
    public static class WeakKnowledgeTag {
        private String tag;
        // 该知识点错误次数
        private Integer wrongCount;
        // 该知识点正确率
        private Double accuracy;
    }
}
