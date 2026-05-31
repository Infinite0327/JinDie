package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class AccuracyTrendVO {
    private Integer days;
    private Map<String, Double> data = new LinkedHashMap<>();
    private Double overallAccuracy;
}
