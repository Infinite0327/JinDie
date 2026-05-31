package kdec.apple.cloud.app.common.dto.portrait;

import java.util.ArrayList;
import java.util.List;

public class WeakPointVO {
    private List<WeakKnowledgeTag> weakPoints = new ArrayList<>();

    public List<WeakKnowledgeTag> getWeakPoints() {
        return weakPoints;
    }

    public void setWeakPoints(List<WeakKnowledgeTag> weakPoints) {
        this.weakPoints = weakPoints;
    }

    public static class WeakKnowledgeTag {
        private String tag;
        private Integer wrongCount;
        private Double accuracy;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Integer getWrongCount() {
            return wrongCount;
        }

        public void setWrongCount(Integer wrongCount) {
            this.wrongCount = wrongCount;
        }

        public Double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(Double accuracy) {
            this.accuracy = accuracy;
        }
    }
}
