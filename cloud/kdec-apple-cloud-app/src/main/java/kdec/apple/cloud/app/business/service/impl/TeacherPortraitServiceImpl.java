package kdec.apple.cloud.app.business.service.impl;

import kdec.apple.cloud.app.business.service.teacher.TeacherPortraitService;
import kdec.apple.cloud.app.common.dto.portrait.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;

public class TeacherPortraitServiceImpl implements TeacherPortraitService {
    @Override
    public StudentPortraitVO getStudentPortrait(Long classId, Long studentId) {
        return null;
    }

    @Override
    public StudyDurationVO getStudyDuration(Long studentId, String granularity, String yearMonth) {
        return null;
    }

    @Override
    public AccuracyTrendVO getAccuracyTrend(Long studentId, Integer days) {
        return null;
    }

    @Override
    public WeakPointVO getWeakPoints(Long studentId) {
        return null;
    }

    @Override
    public ClassPortraitVO getClassPortrait(Long classId) {
        return null;
    }

    @Override
    public WrongAnswerDistributionVO getWrongAnswerDistribution(Long classId) {
        return null;
    }

    @Override
    public ClassWeakKnowledgeVO getWeakKnowledgeHeatmap(Long classId) {
        return null;
    }

    @Override
    public TaskCreatedVO generateTeachingSuggestion(Long classId) {
        return null;
    }

    @Override
    public TeachingSuggestionVO getTeachingSuggestion(Long classId) {
        return null;
    }
}
