package kdec.apple.cloud.app.business.service.teacher;

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
