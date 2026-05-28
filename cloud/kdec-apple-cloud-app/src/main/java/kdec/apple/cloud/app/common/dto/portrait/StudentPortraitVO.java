package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

import java.util.List;

@Data
public class StudentPortraitVO {
    private Long studentId;
    private String studentName;
    // 累计学习时长（分钟）
    private Integer totalStudyMinutes;
    // 累计完成题目数
    private Integer totalExerciseCount;
    // 整体正确率
    private Double overallAccuracy;
    // 连续学习天数
    private Integer continuousStudyDays;
    // 薄弱知识点 Top5
    private List<String> topWeakPoints;
}
