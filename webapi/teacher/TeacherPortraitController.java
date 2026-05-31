package kdec.apple.cloud.app.webapi.teacher;

import kdec.apple.cloud.app.business.service.teacher.TeacherPortraitService;
import kdec.apple.cloud.app.business.service.impl.TeacherPortraitServiceImpl;
import kdec.apple.cloud.app.common.dto.portrait.AccuracyTrendVO;
import kdec.apple.cloud.app.common.dto.portrait.ClassPortraitVO;
import kdec.apple.cloud.app.common.dto.portrait.ClassWeakKnowledgeVO;
import kdec.apple.cloud.app.common.dto.portrait.StudentPortraitVO;
import kdec.apple.cloud.app.common.dto.portrait.StudyDurationVO;
import kdec.apple.cloud.app.common.dto.portrait.TeachingSuggestionVO;
import kdec.apple.cloud.app.common.dto.portrait.WeakPointVO;
import kdec.apple.cloud.app.common.dto.portrait.WrongAnswerDistributionVO;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;

public class TeacherPortraitController {
    private final TeacherPortraitService portraitService = new TeacherPortraitServiceImpl();

    public StudentPortraitVO getStudentPortrait(Long classId, Long studentId) {
        return portraitService.getStudentPortrait(classId, studentId);
    }

    public StudyDurationVO getStudyDuration(Long studentId, String granularity, String yearMonth) {
        return portraitService.getStudyDuration(studentId, granularity, yearMonth);
    }

    public AccuracyTrendVO getAccuracyTrend(Long studentId, Integer days) {
        return portraitService.getAccuracyTrend(studentId, days);
    }

    public WeakPointVO getWeakPoints(Long studentId) {
        return portraitService.getWeakPoints(studentId);
    }

    public ClassPortraitVO getClassPortrait(Long classId) {
        return portraitService.getClassPortrait(classId);
    }

    public WrongAnswerDistributionVO getWrongAnswerDistribution(Long classId) {
        return portraitService.getWrongAnswerDistribution(classId);
    }

    public ClassWeakKnowledgeVO getWeakKnowledgeHeatmap(Long classId) {
        return portraitService.getWeakKnowledgeHeatmap(classId);
    }

    public TaskCreatedVO generateTeachingSuggestion(Long classId) {
        return portraitService.generateTeachingSuggestion(classId);
    }

    public TaskStatusVO getTeachingSuggestionStatus(Long taskId) {
        return portraitService.getTeachingSuggestionStatus(taskId);
    }

    public TeachingSuggestionVO getTeachingSuggestion(Long classId) {
        return portraitService.getTeachingSuggestion(classId);
    }
}
