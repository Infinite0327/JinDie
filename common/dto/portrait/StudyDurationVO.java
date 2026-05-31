package kdec.apple.cloud.app.common.dto.portrait;

import java.util.LinkedHashMap;
import java.util.Map;

public class StudyDurationVO {
    private String granularity;
    private Map<String, Integer> data = new LinkedHashMap<>();
    private Integer totalMinutes;

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public Map<String, Integer> getData() {
        return data;
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }
}
