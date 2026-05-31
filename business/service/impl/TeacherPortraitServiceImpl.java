package kdec.apple.cloud.app.business.service.impl;

import kdec.apple.cloud.app.business.mapper.ClassStudentMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseBatchMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseItemMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseRecordMapper;
import kdec.apple.cloud.app.business.mapper.StudyRecordMapper;
import kdec.apple.cloud.app.business.mapper.TeachingSuggestionMapper;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.business.service.teacher.TeacherPortraitService;
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
import kdec.apple.cloud.app.common.entity.ClassStudent;
import kdec.apple.cloud.app.common.entity.ExerciseBatch;
import kdec.apple.cloud.app.common.entity.ExerciseItem;
import kdec.apple.cloud.app.common.entity.ExerciseRecord;
import kdec.apple.cloud.app.common.entity.StudyRecord;
import kdec.apple.cloud.app.common.entity.TeachingSuggestion;
import kdec.apple.cloud.app.common.enums.ParseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TeacherPortraitServiceImpl implements TeacherPortraitService {
    private static final Map<Long, TaskStatusVO> TASKS = new ConcurrentHashMap<>();

    private final ExerciseRecordMapper exerciseRecordMapper = new ExerciseRecordMapper();
    private final ExerciseItemMapper exerciseItemMapper = new ExerciseItemMapper();
    private final ExerciseBatchMapper exerciseBatchMapper = new ExerciseBatchMapper();
    private final StudyRecordMapper studyRecordMapper = new StudyRecordMapper();
    private final ClassStudentMapper classStudentMapper = new ClassStudentMapper();
    private final TeachingSuggestionMapper teachingSuggestionMapper = new TeachingSuggestionMapper();
    private final AiService aiService = new AiServiceImpl();

    @Override
    public StudentPortraitVO getStudentPortrait(Long classId, Long studentId) {
        ClassStudent student = requireClassStudent(classId, studentId);
        List<StudyRecord> studyRecords = studyRecordMapper.selectByStudentId(studentId);
        List<ExerciseRecord> exerciseRecords = exerciseRecordMapper.selectByStudentIdAndBatchId(studentId, null);

        StudentPortraitVO vo = new StudentPortraitVO();
        vo.setStudentId(studentId);
        vo.setStudentName(student.getStudentName());
        vo.setTotalStudyMinutes(totalStudyMinutes(studyRecords));
        vo.setTotalExerciseCount(exerciseRecords.size());
        vo.setOverallAccuracy(accuracy(exerciseRecords));
        vo.setContinuousStudyDays(continuousStudyDays(studyRecords));
        vo.setTopWeakPoints(topWeakTags(exerciseRecords, 5));
        return vo;
    }

    @Override
    public StudyDurationVO getStudyDuration(Long studentId, String granularity, String yearMonth) {
        String mode = isBlank(granularity) ? "MONTH" : granularity.trim().toUpperCase();
        if (!"DAY".equals(mode) && !"MONTH".equals(mode) && !"YEAR".equals(mode)) {
            throw new IllegalArgumentException("granularity must be DAY, MONTH, or YEAR");
        }

        List<StudyRecord> records = studyRecordMapper.selectByStudentId(studentId);
        Map<String, Integer> data = new LinkedHashMap<>();
        for (StudyRecord record : records) {
            String key = "YEAR".equals(mode)
                    ? record.getStudyDate().toString().substring(0, 7)
                    : record.getStudyDate().toString();
            if (!isBlank(yearMonth) && !"YEAR".equals(mode) && !key.startsWith(yearMonth)) {
                continue;
            }
            data.put(key, data.getOrDefault(key, 0) + record.getDurationMinutes());
        }

        StudyDurationVO vo = new StudyDurationVO();
        vo.setGranularity(mode);
        vo.setData(data);
        vo.setTotalMinutes(sum(data.values()));
        return vo;
    }

    @Override
    public AccuracyTrendVO getAccuracyTrend(Long studentId, Integer days) {
        int range = days == null || days <= 0 ? 30 : days;
        LocalDate startDate = LocalDate.now().minusDays(range - 1L);
        Map<String, List<ExerciseRecord>> byDate = new LinkedHashMap<>();
        for (ExerciseRecord record : exerciseRecordMapper.selectByStudentIdAndBatchId(studentId, null)) {
            if (record.getSubmitTime() == null || record.getSubmitTime().toLocalDate().isBefore(startDate)) {
                continue;
            }
            String key = record.getSubmitTime().toLocalDate().toString();
            byDate.computeIfAbsent(key, ignored -> new ArrayList<>()).add(record);
        }

        Map<String, Double> trend = new LinkedHashMap<>();
        for (Map.Entry<String, List<ExerciseRecord>> entry : byDate.entrySet()) {
            trend.put(entry.getKey(), accuracy(entry.getValue()));
        }

        AccuracyTrendVO vo = new AccuracyTrendVO();
        vo.setDays(range);
        vo.setData(trend);
        vo.setOverallAccuracy(accuracy(exerciseRecordMapper.selectByStudentIdAndBatchId(studentId, null)));
        return vo;
    }

    @Override
    public WeakPointVO getWeakPoints(Long studentId) {
        Map<String, TagStats> stats = tagStats(exerciseRecordMapper.selectByStudentIdAndBatchId(studentId, null));
        List<WeakPointVO.WeakKnowledgeTag> weakPoints = new ArrayList<>();
        for (Map.Entry<String, TagStats> entry : stats.entrySet()) {
            WeakPointVO.WeakKnowledgeTag item = new WeakPointVO.WeakKnowledgeTag();
            item.setTag(entry.getKey());
            item.setWrongCount(entry.getValue().wrongCount);
            item.setAccuracy(entry.getValue().accuracy());
            weakPoints.add(item);
        }
        weakPoints.sort(Comparator.comparing(WeakPointVO.WeakKnowledgeTag::getWrongCount).reversed());

        WeakPointVO vo = new WeakPointVO();
        vo.setWeakPoints(weakPoints);
        return vo;
    }

    @Override
    public ClassPortraitVO getClassPortrait(Long classId) {
        List<Long> studentIds = classStudentIds(classId);
        List<ExerciseRecord> records = recordsForStudents(studentIds);
        List<StudyRecord> studyRecords = studyRecordMapper.selectByStudentIds(studentIds);
        int days = distinctStudyDays(studyRecords);
        int totalExercises = exerciseItemMapper.selectByCondition(null, null, null).size();

        ClassPortraitVO vo = new ClassPortraitVO();
        vo.setClassId(classId);
        vo.setClassName("Class " + classId);
        vo.setStudentCount(studentIds.size());
        vo.setAvgStudyMinutesPerDay(round(studentIds.isEmpty() || days == 0
                ? 0D
                : totalStudyMinutes(studyRecords) * 1D / studentIds.size() / days));
        vo.setAvgAccuracy(accuracy(records));
        vo.setAvgExerciseCompletion(round(studentIds.isEmpty() || totalExercises == 0
                ? 0D
                : records.size() * 1D / studentIds.size() / totalExercises));
        return vo;
    }

    @Override
    public WrongAnswerDistributionVO getWrongAnswerDistribution(Long classId) {
        Map<Long, WrongAnswerDistributionVO.ChapterWrongDistribution> chapters = new LinkedHashMap<>();
        for (ExerciseRecord record : recordsForStudents(classStudentIds(classId))) {
            if (Boolean.TRUE.equals(record.getCorrect())) {
                continue;
            }
            ExerciseItem item = exerciseItemMapper.selectById(record.getExerciseItemId());
            ExerciseBatch batch = exerciseBatchMapper.selectById(record.getBatchId());
            if (item == null || batch == null) {
                continue;
            }
            Long chapterId = batch.getChapterId();
            WrongAnswerDistributionVO.ChapterWrongDistribution chapter = chapters.computeIfAbsent(
                    chapterId,
                    ignored -> newChapterDistribution(chapterId)
            );
            String type = item.getType() == null ? "UNKNOWN" : item.getType().name();
            chapter.getByExerciseType().put(type, chapter.getByExerciseType().getOrDefault(type, 0) + 1);
            chapter.setTotalWrongCount(chapter.getTotalWrongCount() + 1);
        }

        WrongAnswerDistributionVO vo = new WrongAnswerDistributionVO();
        vo.setChapters(new ArrayList<>(chapters.values()));
        return vo;
    }

    @Override
    public ClassWeakKnowledgeVO getWeakKnowledgeHeatmap(Long classId) {
        List<Long> studentIds = classStudentIds(classId);
        Map<Long, Map<String, TagStats>> chapterStats = new LinkedHashMap<>();
        Map<Long, Map<String, Set<Long>>> wrongStudents = new HashMap<>();

        for (ExerciseRecord record : recordsForStudents(studentIds)) {
            ExerciseItem item = exerciseItemMapper.selectById(record.getExerciseItemId());
            ExerciseBatch batch = exerciseBatchMapper.selectById(record.getBatchId());
            if (item == null || batch == null) {
                continue;
            }
            Long chapterId = batch.getChapterId();
            for (String tag : tags(item.getKnowledgeTags())) {
                chapterStats.computeIfAbsent(chapterId, ignored -> new LinkedHashMap<>())
                        .computeIfAbsent(tag, ignored -> new TagStats())
                        .add(Boolean.TRUE.equals(record.getCorrect()));
                if (!Boolean.TRUE.equals(record.getCorrect())) {
                    wrongStudents.computeIfAbsent(chapterId, ignored -> new HashMap<>())
                            .computeIfAbsent(tag, ignored -> new HashSet<>())
                            .add(record.getStudentId());
                }
            }
        }

        List<ClassWeakKnowledgeVO.ChapterWeakKnowledge> chapters = new ArrayList<>();
        for (Map.Entry<Long, Map<String, TagStats>> chapterEntry : chapterStats.entrySet()) {
            ClassWeakKnowledgeVO.ChapterWeakKnowledge chapter = new ClassWeakKnowledgeVO.ChapterWeakKnowledge();
            chapter.setChapterId(chapterEntry.getKey());
            chapter.setChapterTitle("Chapter " + chapterEntry.getKey());
            List<ClassWeakKnowledgeVO.KnowledgeHeatItem> items = new ArrayList<>();
            for (Map.Entry<String, TagStats> tagEntry : chapterEntry.getValue().entrySet()) {
                ClassWeakKnowledgeVO.KnowledgeHeatItem item = new ClassWeakKnowledgeVO.KnowledgeHeatItem();
                item.setTag(tagEntry.getKey());
                item.setAvgAccuracy(tagEntry.getValue().accuracy());
                item.setWrongStudentCount(wrongStudents.getOrDefault(chapterEntry.getKey(), Collections.emptyMap())
                        .getOrDefault(tagEntry.getKey(), Collections.emptySet()).size());
                item.setHeatLevel(heatLevel(tagEntry.getValue().accuracy()));
                items.add(item);
            }
            items.sort(Comparator.comparing(ClassWeakKnowledgeVO.KnowledgeHeatItem::getHeatLevel).reversed());
            chapter.setWeakPoints(items);
            chapters.add(chapter);
        }

        ClassWeakKnowledgeVO vo = new ClassWeakKnowledgeVO();
        vo.setChapters(chapters);
        return vo;
    }

    @Override
    public TaskCreatedVO generateTeachingSuggestion(Long classId) {
        Long taskId = System.currentTimeMillis();
        TaskStatusVO status = new TaskStatusVO();
        status.setTaskId(taskId);
        status.setBizId(classId);
        status.setTaskType("TEACHING_SUGGESTION");
        status.setStatus(ParseStatus.PARSING);
        TASKS.put(taskId, status);

        TeachingSuggestion suggestion = buildSuggestion(classId);
        teachingSuggestionMapper.save(suggestion);
        status.setStatus(ParseStatus.SUCCESS);

        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(taskId);
        vo.setBizId(classId);
        vo.setTaskType("TEACHING_SUGGESTION");
        vo.setEstimatedSeconds(0);
        return vo;
    }

    @Override
    public TaskStatusVO getTeachingSuggestionStatus(Long taskId) {
        TaskStatusVO status = TASKS.get(taskId);
        if (status == null) {
            throw new IllegalArgumentException("task not found");
        }
        return status;
    }

    @Override
    public TeachingSuggestionVO getTeachingSuggestion(Long classId) {
        TeachingSuggestion suggestion = teachingSuggestionMapper.selectByClassId(classId);
        if (suggestion == null) {
            throw new IllegalArgumentException("teaching suggestion not found");
        }
        TeachingSuggestionVO vo = new TeachingSuggestionVO();
        vo.setClassId(classId);
        vo.setContent(suggestion.getContent());
        vo.setSuggestedKnowledgeTags(suggestion.getSuggestedKnowledgeTags());
        vo.setGeneratedAt(suggestion.getCreateTime());

        List<TeachingSuggestionVO.FocusStudentVO> focusStudents = new ArrayList<>();
        for (Long studentId : suggestion.getFocusStudentIds()) {
            ClassStudent student = classStudentMapper.selectByClassIdAndStudentId(classId, studentId);
            TeachingSuggestionVO.FocusStudentVO focus = new TeachingSuggestionVO.FocusStudentVO();
            focus.setStudentId(studentId);
            focus.setStudentName(student == null ? "Student " + studentId : student.getStudentName());
            focus.setReason("Accuracy is below the class target.");
            focusStudents.add(focus);
        }
        vo.setFocusStudents(focusStudents);
        return vo;
    }

    private TeachingSuggestion buildSuggestion(Long classId) {
        ClassPortraitVO classPortrait = getClassPortrait(classId);
        List<String> weakTags = topWeakTags(recordsForStudents(classStudentIds(classId)), 5);
        List<Long> focusStudents = new ArrayList<>();
        for (Long studentId : classStudentIds(classId)) {
            if (getStudentPortrait(classId, studentId).getOverallAccuracy() < 0.6D) {
                focusStudents.add(studentId);
            }
        }

        String summary = "classId=" + classId
                + ", studentCount=" + classPortrait.getStudentCount()
                + ", avgAccuracy=" + classPortrait.getAvgAccuracy()
                + ", avgStudyMinutesPerDay=" + classPortrait.getAvgStudyMinutesPerDay()
                + ", weakKnowledgeTags=" + weakTags
                + ", focusStudentIds=" + focusStudents;
        String content;
        try {
            content = aiService.generateTeachingSuggestion(classId, summary);
        } catch (RuntimeException e) {
            content = "## Teaching suggestion\n"
                    + "Prioritize the weak knowledge points: " + weakTags + ".\n"
                    + "Arrange targeted review and short follow-up exercises for students: " + focusStudents + ".";
        }

        TeachingSuggestion suggestion = new TeachingSuggestion();
        suggestion.setClassId(classId);
        suggestion.setContent(content);
        suggestion.setSuggestedKnowledgeTags(weakTags);
        suggestion.setFocusStudentIds(focusStudents);
        suggestion.setCreateTime(LocalDateTime.now());
        return suggestion;
    }

    private ClassStudent requireClassStudent(Long classId, Long studentId) {
        ClassStudent student = classStudentMapper.selectByClassIdAndStudentId(classId, studentId);
        if (student == null) {
            throw new IllegalArgumentException("student does not belong to class");
        }
        return student;
    }

    private List<Long> classStudentIds(Long classId) {
        List<Long> result = new ArrayList<>();
        for (ClassStudent student : classStudentMapper.selectByClassId(classId)) {
            result.add(student.getStudentId());
        }
        return result;
    }

    private List<ExerciseRecord> recordsForStudents(List<Long> studentIds) {
        List<ExerciseRecord> result = new ArrayList<>();
        for (ExerciseRecord record : exerciseRecordMapper.selectAll()) {
            if (studentIds.contains(record.getStudentId())) {
                result.add(record);
            }
        }
        return result;
    }

    private Map<String, TagStats> tagStats(List<ExerciseRecord> records) {
        Map<String, TagStats> result = new LinkedHashMap<>();
        for (ExerciseRecord record : records) {
            ExerciseItem item = exerciseItemMapper.selectById(record.getExerciseItemId());
            if (item == null) {
                continue;
            }
            for (String tag : tags(item.getKnowledgeTags())) {
                result.computeIfAbsent(tag, ignored -> new TagStats())
                        .add(Boolean.TRUE.equals(record.getCorrect()));
            }
        }
        return result;
    }

    private List<String> topWeakTags(List<ExerciseRecord> records, int limit) {
        List<Map.Entry<String, TagStats>> entries = new ArrayList<>(tagStats(records).entrySet());
        entries.sort((left, right) -> {
            int wrongCompare = Integer.compare(right.getValue().wrongCount, left.getValue().wrongCount);
            return wrongCompare != 0 ? wrongCompare : Double.compare(left.getValue().accuracy(), right.getValue().accuracy());
        });
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, TagStats> entry : entries) {
            if (entry.getValue().wrongCount > 0 && result.size() < limit) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private List<String> tags(String rawTags) {
        if (isBlank(rawTags)) {
            return Collections.emptyList();
        }
        String cleaned = rawTags.replace("[", "").replace("]", "").replace("\"", "");
        List<String> result = new ArrayList<>();
        for (String tag : cleaned.split(",")) {
            if (!tag.trim().isEmpty()) {
                result.add(tag.trim());
            }
        }
        return result;
    }

    private WrongAnswerDistributionVO.ChapterWrongDistribution newChapterDistribution(Long chapterId) {
        WrongAnswerDistributionVO.ChapterWrongDistribution chapter =
                new WrongAnswerDistributionVO.ChapterWrongDistribution();
        chapter.setChapterId(chapterId);
        chapter.setChapterTitle("Chapter " + chapterId);
        chapter.setTotalWrongCount(0);
        return chapter;
    }

    private int totalStudyMinutes(List<StudyRecord> records) {
        int result = 0;
        for (StudyRecord record : records) {
            result += record.getDurationMinutes();
        }
        return result;
    }

    private int distinctStudyDays(List<StudyRecord> records) {
        Set<LocalDate> days = new HashSet<>();
        for (StudyRecord record : records) {
            days.add(record.getStudyDate());
        }
        return days.size();
    }

    private int continuousStudyDays(List<StudyRecord> records) {
        Set<LocalDate> studyDays = new HashSet<>();
        for (StudyRecord record : records) {
            studyDays.add(record.getStudyDate());
        }
        int count = 0;
        LocalDate date = LocalDate.now();
        while (studyDays.contains(date)) {
            count++;
            date = date.minusDays(1);
        }
        return count;
    }

    private Double accuracy(List<ExerciseRecord> records) {
        if (records.isEmpty()) {
            return 0D;
        }
        int correct = 0;
        for (ExerciseRecord record : records) {
            if (Boolean.TRUE.equals(record.getCorrect())) {
                correct++;
            }
        }
        return round(correct * 1D / records.size());
    }

    private int heatLevel(Double accuracy) {
        double value = accuracy == null ? 0D : accuracy;
        if (value < 0.2D) {
            return 4;
        }
        if (value < 0.4D) {
            return 3;
        }
        if (value < 0.6D) {
            return 2;
        }
        if (value < 0.8D) {
            return 1;
        }
        return 0;
    }

    private int sum(Iterable<Integer> values) {
        int result = 0;
        for (Integer value : values) {
            result += value;
        }
        return result;
    }

    private Double round(Double value) {
        return Math.round(value * 10000D) / 10000D;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class TagStats {
        private int totalCount;
        private int correctCount;
        private int wrongCount;

        private void add(boolean correct) {
            totalCount++;
            if (correct) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }

        private Double accuracy() {
            if (totalCount == 0) {
                return 0D;
            }
            return Math.round(correctCount * 10000D / totalCount) / 10000D;
        }
    }
}
