package kdec.apple.cloud.app.common.dto.portrait;

import lombok.Data;

@Data
public class ClassPortraitVO {
    private Long classId;
    private String className;
    private Integer studentCount;
    private Double avgStudyMinutesPerDay;
    private Double avgAccuracy;
    private Double avgExerciseCompletion; // 平均题目完成率
}
