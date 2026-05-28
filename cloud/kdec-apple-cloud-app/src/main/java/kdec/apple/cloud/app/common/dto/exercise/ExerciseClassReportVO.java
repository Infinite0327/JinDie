package kdec.apple.cloud.app.common.dto.exercise;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExerciseClassReportVO {
    // 班级整体数据
    private Integer submittedCount;      // 已提交人数
    private Integer notSubmittedCount;   // 未提交人数
    private Double avgScore;             // 班级平均分
    private Double avgAccuracy;          // 班级平均正确率
    // 每道题的统计
    private List<ExerciseItemReportVO> exerciseReports;
    // 每个学生的总成绩
    private List<StudentGradeVO> studentGrades;

    //TODO:等着xyn设计
    @Data
    public static class StudentGradeVO {
//        private Long studentId;
//        private String studentName;
//        private List<String> studentAnswer;  // 学生的答案
//        private List<String> correctAnswer;  // 正确答案
//        private Integer score;               // 得分
//        private Integer totalScore;          // 该题满分
//        private String aiFeedback;           // AI批改反馈
    }
}
