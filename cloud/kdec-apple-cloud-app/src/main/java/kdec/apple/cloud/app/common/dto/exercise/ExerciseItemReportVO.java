package kdec.apple.cloud.app.common.dto.exercise;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExerciseItemReportVO {
    private Long exerciseId;
    private String content;              // 题干
    private String type;
    private Double accuracy;             // 该题正确率
    private Integer wrongCount;          // 错误人数
    // 选项分布（客观题）
    private Map<String, Integer> optionDistribution;
    private List<String> commonMistakes; // AI分析的常见错误（主观题）
}
