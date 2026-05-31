package kdec.apple.cloud.app.common.dto.portrait;

import java.util.LinkedHashMap;
import java.util.Map;

public class AccuracyTrendVO {
    private Integer days;
    private Map<String, Double> data = new LinkedHashMap<>();
    private Double overallAccuracy;

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Map<String, Double> getData() {
        return data;
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }

    public Double getOverallAccuracy() {
        return overallAccuracy;
    }

    public void setOverallAccuracy(Double overallAccuracy) {
        this.overallAccuracy = overallAccuracy;
    }
}
