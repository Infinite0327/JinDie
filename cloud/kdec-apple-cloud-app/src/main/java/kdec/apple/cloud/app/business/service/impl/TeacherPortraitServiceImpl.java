package kdec.apple.cloud.app.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.cloud.app.business.mapper.ChapterMapper;
import kdec.apple.cloud.app.business.mapper.ClassMapper;
import kdec.apple.cloud.app.business.mapper.ClassStudentMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseBatchMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseRecordMapper;
import kdec.apple.cloud.app.business.mapper.StudyRecordMapper;
import kdec.apple.cloud.app.business.mapper.TaskMapper;
import kdec.apple.cloud.app.business.mapper.TeachingSuggestionMapper;
import kdec.apple.cloud.app.business.mapper.UserMapper;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.business.service.teacher.TeacherPortraitService;
import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.common.dto.portrait.*;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.common.dto.utils.TaskStatusVO;
import kdec.apple.cloud.app.entity.*;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherPortraitServiceImpl implements TeacherPortraitService {
    private final ClassStudentMapper classStudentMapper;
    private final StudyRecordMapper studyRecordMapper;
    private final ExerciseRecordMapper exerciseRecordMapper;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseBatchMapper exerciseBatchMapper;
    private final ChapterMapper chapterMapper;
    private final ClassMapper classMapper;
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;
    private final TeachingSuggestionMapper teachingSuggestionMapper;
    private final AiService aiService;

    @Override
    public StudentPortraitVO getStudentPortrait(Long classId, Long studentId) {
        requireClassStudent(classId, studentId);
        List<StudyRecord> studies = studiesByStudent(studentId);
        List<ExerciseRecord> exercises = exercisesByStudent(studentId);
        StudentPortraitVO vo = new StudentPortraitVO();
        vo.setStudentId(studentId);
        User user = userMapper.selectById(studentId);
        vo.setStudentName(user == null ? "" : user.getName());
        vo.setTotalStudyMinutes(totalMinutes(studies));
        vo.setTotalExerciseCount(exercises.size());
        vo.setOverallAccuracy(accuracy(exercises));
        vo.setContinuousStudyDays(continuousDays(studies));
        vo.setTopWeakPoints(topWeakTags(exercises, 5));
        return vo;
    }

    @Override
    public StudyDurationVO getStudyDuration(Long studentId, String granularity, String yearMonth) {
        String mode = granularity == null ? "MONTH" : granularity.trim().toUpperCase();
        if (!Arrays.asList("DAY", "MONTH", "YEAR").contains(mode)) {
            throw new IllegalArgumentException("granularity must be DAY, MONTH, or YEAR");
        }
        Map<String, Integer> data = new LinkedHashMap<>();
        for (StudyRecord record : studiesByStudent(studentId)) {
            String date = record.getStudyDate().toString();
            String key = "YEAR".equals(mode) ? date.substring(0, 7) : date;
            if (yearMonth != null && !"YEAR".equals(mode) && !key.startsWith(yearMonth)) {
                continue;
            }
            data.put(key, data.getOrDefault(key, 0) + record.getDurationMinutes());
        }
        StudyDurationVO vo = new StudyDurationVO();
        vo.setGranularity(mode);
        vo.setData(data);
        vo.setTotalMinutes(data.values().stream().mapToInt(Integer::intValue).sum());
        return vo;
    }

    @Override
    public AccuracyTrendVO getAccuracyTrend(Long studentId, Integer days) {
        int range = days == null || days <= 0 ? 30 : days;
        LocalDate start = LocalDate.now().minusDays(range - 1L);
        Map<String, List<ExerciseRecord>> groups = exercisesByStudent(studentId).stream()
                .filter(record -> record.getSubmitTime() != null && !record.getSubmitTime().toLocalDate().isBefore(start))
                .collect(Collectors.groupingBy(record -> record.getSubmitTime().toLocalDate().toString(),
                        LinkedHashMap::new, Collectors.toList()));
        AccuracyTrendVO vo = new AccuracyTrendVO();
        vo.setDays(range);
        groups.forEach((key, value) -> vo.getData().put(key, accuracy(value)));
        vo.setOverallAccuracy(accuracy(exercisesByStudent(studentId)));
        return vo;
    }

    @Override
    public WeakPointVO getWeakPoints(Long studentId) {
        List<WeakPointVO.WeakKnowledgeTag> items = new ArrayList<>();
        tagStats(exercisesByStudent(studentId)).forEach((tag, stat) -> {
            WeakPointVO.WeakKnowledgeTag item = new WeakPointVO.WeakKnowledgeTag();
            item.setTag(tag);
            item.setWrongCount(stat.wrong);
            item.setAccuracy(stat.accuracy());
            items.add(item);
        });
        items.sort(Comparator.comparing(WeakPointVO.WeakKnowledgeTag::getWrongCount).reversed());
        WeakPointVO vo = new WeakPointVO();
        vo.setWeakPoints(items);
        return vo;
    }

    @Override
    public ClassPortraitVO getClassPortrait(Long classId) {
        List<Long> studentIds = studentIds(classId);
        List<StudyRecord> studies = studiesByStudents(studentIds);
        List<ExerciseRecord> exercises = exercisesByStudents(studentIds);
        Set<LocalDate> studyDays = studies.stream().map(StudyRecord::getStudyDate).collect(Collectors.toSet());
        long totalItems = exerciseMapper.selectCount(new LambdaQueryWrapper<>());
        ClassPortraitVO vo = new ClassPortraitVO();
        Course course = classMapper.selectById(classId);
        vo.setClassId(classId);
        vo.setClassName(course == null ? "" : course.getName());
        vo.setStudentCount(studentIds.size());
        vo.setAvgStudyMinutesPerDay(round(studentIds.isEmpty() || studyDays.isEmpty() ? 0D
                : totalMinutes(studies) * 1D / studentIds.size() / studyDays.size()));
        vo.setAvgAccuracy(accuracy(exercises));
        vo.setAvgExerciseCompletion(round(studentIds.isEmpty() || totalItems == 0 ? 0D
                : exercises.size() * 1D / studentIds.size() / totalItems));
        return vo;
    }

    @Override
    public WrongAnswerDistributionVO getWrongAnswerDistribution(Long classId) {
        Map<Long, WrongAnswerDistributionVO.ChapterWrongDistribution> chapters = new LinkedHashMap<>();
        for (ExerciseRecord record : exercisesByStudents(studentIds(classId))) {
            if (Boolean.TRUE.equals(record.getCorrect())) continue;
            ExerciseItem item = exerciseMapper.selectById(record.getExerciseItemId());
            ExerciseBatch batch = exerciseBatchMapper.selectById(record.getBatchId());
            if (item == null || batch == null) continue;
            Long chapterId = batch.getChapterId();
            WrongAnswerDistributionVO.ChapterWrongDistribution chapter = chapters.computeIfAbsent(chapterId, this::chapterWrong);
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
        Map<Long, Map<String, Stats>> chapters = new LinkedHashMap<>();
        Map<Long, Map<String, Set<Long>>> wrongStudents = new HashMap<>();
        for (ExerciseRecord record : exercisesByStudents(studentIds(classId))) {
            ExerciseItem item = exerciseMapper.selectById(record.getExerciseItemId());
            ExerciseBatch batch = exerciseBatchMapper.selectById(record.getBatchId());
            if (item == null || batch == null) continue;
            for (String tag : tags(item.getKnowledgeTags())) {
                chapters.computeIfAbsent(batch.getChapterId(), ignored -> new LinkedHashMap<>())
                        .computeIfAbsent(tag, ignored -> new Stats()).add(Boolean.TRUE.equals(record.getCorrect()));
                if (!Boolean.TRUE.equals(record.getCorrect())) {
                    wrongStudents.computeIfAbsent(batch.getChapterId(), ignored -> new HashMap<>())
                            .computeIfAbsent(tag, ignored -> new HashSet<>()).add(record.getStudentId());
                }
            }
        }
        ClassWeakKnowledgeVO vo = new ClassWeakKnowledgeVO();
        List<ClassWeakKnowledgeVO.ChapterWeakKnowledge> chapterItems = new ArrayList<>();
        chapters.forEach((chapterId, statMap) -> {
            ClassWeakKnowledgeVO.ChapterWeakKnowledge chapter = new ClassWeakKnowledgeVO.ChapterWeakKnowledge();
            Chapter entity = chapterMapper.selectById(chapterId);
            chapter.setChapterId(chapterId);
            chapter.setChapterTitle(entity == null ? "" : entity.getTitle());
            List<ClassWeakKnowledgeVO.KnowledgeHeatItem> heatItems = new ArrayList<>();
            statMap.forEach((tag, stat) -> {
                ClassWeakKnowledgeVO.KnowledgeHeatItem item = new ClassWeakKnowledgeVO.KnowledgeHeatItem();
                item.setTag(tag);
                item.setAvgAccuracy(stat.accuracy());
                item.setWrongStudentCount(wrongStudents.getOrDefault(chapterId, Collections.emptyMap())
                        .getOrDefault(tag, Collections.emptySet()).size());
                item.setHeatLevel(heatLevel(stat.accuracy()));
                heatItems.add(item);
            });
            chapter.setWeakPoints(heatItems);
            chapterItems.add(chapter);
        });
        vo.setChapters(chapterItems);
        return vo;
    }

    @Override
    public TaskCreatedVO generateTeachingSuggestion(Long classId) {
        Task task = Task.builder().taskType(TaskType.TEACHING_SUGGESTION).status(ParseStatus.PROCESSING)
                .bizId(classId).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
        taskMapper.insert(task);
        List<String> weakTags = topWeakTags(exercisesByStudents(studentIds(classId)), 5);
        String summary = "portrait=" + JSON.toJSONString(getClassPortrait(classId)) + ", weakTags=" + weakTags;
        String content;
        try {
            content = aiService.generateTeachingSuggestion(classId, summary);
            task.setStatus(ParseStatus.DONE);
        } catch (RuntimeException e) {
            content = "## Teaching suggestion\nPrioritize weak knowledge points: " + weakTags;
            task.setStatus(ParseStatus.DONE);
            task.setFailReason("AI fallback: " + e.getMessage());
        }
        teachingSuggestionMapper.insert(TeachingSuggestion.builder().classId(classId).content(content)
                .focusStudents("[]").suggestedKnowledgeTags(JSON.toJSONString(weakTags))
                .createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateById(task);
        return TaskCreatedVO.builder().taskId(task.getTaskId()).bizId(classId)
                .taskType(TaskType.TEACHING_SUGGESTION).estimatedSeconds(0).build();
    }

    @Override
    public TaskStatusVO getTeachingSuggestionStatus(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) throw new IllegalArgumentException("task not found");
        return TaskStatusVO.builder().taskId(taskId).bizId(task.getBizId()).taskType(task.getTaskType())
                .status(task.getStatus()).failReason(task.getFailReason()).build();
    }

    @Override
    public TeachingSuggestionVO getTeachingSuggestion(Long classId) {
        TeachingSuggestion entity = teachingSuggestionMapper.selectOne(new LambdaQueryWrapper<TeachingSuggestion>()
                .eq(TeachingSuggestion::getClassId, classId).orderByDesc(TeachingSuggestion::getCreateTime).last("LIMIT 1"));
        if (entity == null) throw new IllegalArgumentException("teaching suggestion not found");
        TeachingSuggestionVO vo = new TeachingSuggestionVO();
        vo.setClassId(classId);
        vo.setContent(entity.getContent());
        vo.setFocusStudents(JSON.parseArray(entity.getFocusStudents(), TeachingSuggestionVO.FocusStudentVO.class));
        vo.setSuggestedKnowledgeTags(JSON.parseArray(entity.getSuggestedKnowledgeTags(), String.class));
        vo.setGeneratedAt(entity.getCreateTime());
        return vo;
    }

    private void requireClassStudent(Long classId, Long studentId) {
        if (classStudentMapper.selectCount(new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId)) == 0) throw new IllegalArgumentException("student does not belong to class");
    }
    private List<Long> studentIds(Long classId) { return classStudentMapper.selectList(new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, classId)).stream().map(ClassStudent::getStudentId).collect(Collectors.toList()); }
    private List<StudyRecord> studiesByStudent(Long id) { return studyRecordMapper.selectList(new LambdaQueryWrapper<StudyRecord>().eq(StudyRecord::getStudentId, id)); }
    private List<StudyRecord> studiesByStudents(List<Long> ids) { return ids.isEmpty() ? Collections.emptyList() : studyRecordMapper.selectList(new LambdaQueryWrapper<StudyRecord>().in(StudyRecord::getStudentId, ids)); }
    private List<ExerciseRecord> exercisesByStudent(Long id) { return exerciseRecordMapper.selectList(new LambdaQueryWrapper<ExerciseRecord>().eq(ExerciseRecord::getStudentId, id)); }
    private List<ExerciseRecord> exercisesByStudents(List<Long> ids) { return ids.isEmpty() ? Collections.emptyList() : exerciseRecordMapper.selectList(new LambdaQueryWrapper<ExerciseRecord>().in(ExerciseRecord::getStudentId, ids)); }
    private int totalMinutes(List<StudyRecord> list) { return list.stream().mapToInt(StudyRecord::getDurationMinutes).sum(); }
    private int continuousDays(List<StudyRecord> list) { Set<LocalDate> days=list.stream().map(StudyRecord::getStudyDate).collect(Collectors.toSet()); int count=0; for(LocalDate d=LocalDate.now();days.contains(d);d=d.minusDays(1)) count++; return count; }
    private double accuracy(List<ExerciseRecord> list) { return list.isEmpty()?0D:round(list.stream().filter(r->Boolean.TRUE.equals(r.getCorrect())).count()*1D/list.size()); }
    private Map<String,Stats> tagStats(List<ExerciseRecord> list) { Map<String,Stats> map=new LinkedHashMap<>(); for(ExerciseRecord r:list){ ExerciseItem i=exerciseMapper.selectById(r.getExerciseItemId()); if(i!=null) for(String t:tags(i.getKnowledgeTags())) map.computeIfAbsent(t,k->new Stats()).add(Boolean.TRUE.equals(r.getCorrect())); } return map; }
    private List<String> topWeakTags(List<ExerciseRecord> list,int limit){ return tagStats(list).entrySet().stream().filter(e->e.getValue().wrong>0).sorted((a,b)->Integer.compare(b.getValue().wrong,a.getValue().wrong)).limit(limit).map(Map.Entry::getKey).collect(Collectors.toList()); }
    private List<String> tags(String raw){ return raw==null?Collections.emptyList():Arrays.stream(raw.replace("[","").replace("]","").replace("\"","").split(",")).map(String::trim).filter(s->!s.isEmpty()).collect(Collectors.toList()); }
    private int heatLevel(double a){ return a<.2?4:a<.4?3:a<.6?2:a<.8?1:0; }
    private double round(double v){ return Math.round(v*10000D)/10000D; }
    private WrongAnswerDistributionVO.ChapterWrongDistribution chapterWrong(Long id){ WrongAnswerDistributionVO.ChapterWrongDistribution v=new WrongAnswerDistributionVO.ChapterWrongDistribution(); Chapter c=chapterMapper.selectById(id); v.setChapterId(id); v.setChapterTitle(c==null?"":c.getTitle()); v.setByExerciseType(new LinkedHashMap<>()); v.setTotalWrongCount(0); return v; }
    private static class Stats { int total; int correct; int wrong; void add(boolean ok){total++;if(ok)correct++;else wrong++;} double accuracy(){return total==0?0D:Math.round(correct*10000D/total)/10000D;} }
}
