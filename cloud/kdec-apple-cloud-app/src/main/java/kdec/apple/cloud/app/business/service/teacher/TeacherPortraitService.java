package kdec.apple.cloud.app.business.service.teacher;

import kdec.apple.cloud.app.common.dto.portrait.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;

public interface TeacherPortraitService {
    StudentPortraitVO getStudentPortrait(Long classId, Long studentId);

    StudyDurationVO getStudyDuration(Long studentId, String granularity, String yearMonth);

    AccuracyTrendVO getAccuracyTrend(Long studentId, Integer days);

    WeakPointVO getWeakPoints(Long studentId);

    ClassPortraitVO getClassPortrait(Long classId);

    WrongAnswerDistributionVO getWrongAnswerDistribution(Long classId);

    ClassWeakKnowledgeVO getWeakKnowledgeHeatmap(Long classId);

    TaskCreatedVO generateTeachingSuggestion(Long classId);

    TaskStatusVO getTeachingSuggestionStatus(Long taskId);

    TeachingSuggestionVO getTeachingSuggestion(Long classId);
}
