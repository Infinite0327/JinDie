package kdec.apple.cloud.app.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kdec.apple.base.common.exception.BusinessException;
import kdec.apple.base.common.exception.ErrorCode;
import kdec.apple.base.common.utils.OssUtil;
import kdec.apple.cloud.app.business.mapper.*;
import kdec.apple.cloud.app.business.service.AiService;
import kdec.apple.cloud.app.business.service.teacher.TeacherExerciseService;
import kdec.apple.cloud.app.common.dto.answer.*;
import kdec.apple.cloud.app.common.dto.enums.TaskType;
import kdec.apple.cloud.app.common.dto.exercise.*;
import kdec.apple.cloud.app.common.dto.utils.RedisUtil;
import kdec.apple.cloud.app.common.dto.utils.TaskCreatedVO;
import kdec.apple.cloud.app.entity.Chapter;
import kdec.apple.cloud.app.entity.ExerciseBatch;
import kdec.apple.cloud.app.entity.ExerciseItem;
import kdec.apple.cloud.app.entity.Task;
import kdec.apple.cloud.app.entity.enums.FileType;
import kdec.apple.cloud.app.entity.enums.ParseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherExerciseServiceImpl implements TeacherExerciseService {

    private final ExerciseMapper exerciseMapper;
    private final TaskMapper taskMapper;
    private final ChapterMapper chapterMapper;
    private final AiService aiService;
    private final OssUtil ossUtil;
    private final ExerciseBatchMapper exerciseBatchMapper;
    private final RedisUtil redisUtil;
    private final AnswerBatchMapper answerBatchMapper;
    private final AnswerItemMapper answerMapper;

    //习题部分


    @Override
    public TaskCreatedVO upload(ExerciseUploadDTO dto, MultipartFile file) {
        // 1. 校验章节
        Chapter chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        // 2. 上传文件到OSS
        String ossUrl = ossUtil.upload(file);

        // 3. 先创建ExerciseBatch
        ExerciseBatch batch = ExerciseBatch.builder()
                .chapterId(dto.getChapterId())
                .ossUrl(ossUrl)
                .parseStatus(ParseStatus.PENDING)
                .deadline(dto.getDeadline())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        exerciseBatchMapper.insert(batch);

        // 4. 再创建Task，bizId指向batchId
        Task task = Task.builder()
                .taskType(TaskType.EXERCISE_PARSE)
                .status(ParseStatus.PENDING)
                .bizId(batch.getId())  // 指向batchId
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);

        // 5. 回填taskId到batch
        batch.setTaskId(task.getTaskId());
        exerciseBatchMapper.updateById(batch);

        // 6. 异步触发AI解析
        aiService.parseExerciseAsync(task.getTaskId(), ossUrl, batch.getId());

        // 7. 返回
        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.EXERCISE_PARSE);
        vo.setBizId(batch.getId());
        return vo;
    }

    @Override
    public List<ExerciseItem> getPreview(Long taskId) {
        // 从缓存（Redis）取AI解析结果
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        if (task.getStatus() != ParseStatus.DONE) {
            throw new BusinessException(ErrorCode.TASK_NOT_DONE);
        }
        return aiService.getExercisePreview(taskId);
    }

    @Override
    public ExerciseBatchResultVO confirm(ExerciseConfirmDTO dto) {
        // 1. 校验batch是否存在
        ExerciseBatch batch = exerciseBatchMapper.selectById(dto.getBatchId());
        if (batch == null) {
            throw new BusinessException(ErrorCode.EXERCISE_BATCH_NOT_FOUND);
        }

        // 2. 批量写入ExerciseItem
        int successCount = 0;
        int failCount = 0;

        for (ExerciseItem item : dto.getExercises()) {
            try {
                item.setBatchId(dto.getBatchId());
                exerciseMapper.insert(item);
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        // 3. 更新ExerciseBatch状态和信息
        batch.setTotalCount(successCount);
        batch.setDeadline(dto.getDeadline());
        batch.setParseStatus(ParseStatus.DONE);
        batch.setUpdateTime(LocalDateTime.now());
        exerciseBatchMapper.updateById(batch);

        // 4. 清除Redis缓存
        redisUtil.delete("exercise:preview:" + dto.getTaskId());

        // 5. 返回结果
        ExerciseBatchResultVO vo = new ExerciseBatchResultVO();
        vo.setBatchId(dto.getBatchId());
        vo.setSuccessCount(successCount);
        vo.setFailCount(failCount);
        return vo;
    }

    @Override
    public void update(Long exerciseId, ExerciseItem item) {
        ExerciseItem exercise = exerciseMapper.selectById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.EXERCISE_NOT_FOUND);
        }
        exercise.setContent(item.getContent());
        exercise.setOptions(JSON.toJSONString(item.getOptions()));
        exercise.setKnowledgeTags(JSON.toJSONString(item.getKnowledgeTags()));
        exerciseMapper.updateById(exercise);
    }

    @Override
    public void deleteBatch(Long batchId) {
        // 1. 校验批次是否存在
        ExerciseBatch batch = exerciseBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(ErrorCode.EXERCISE_BATCH_NOT_FOUND);
        }

        // 2. 删除OSS文件
        ossUtil.delete(batch.getOssUrl());

        // 3. 删除该批次下所有ExerciseItem
        LambdaQueryWrapper<ExerciseItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExerciseItem::getBatchId, batchId);
        exerciseMapper.delete(wrapper);

        // 4. 删除ExerciseBatch
        exerciseBatchMapper.deleteById(batchId);

    }

    @Override
    public void deleteExercise(Long itemId) {
        ExerciseItem exercise = exerciseMapper.selectById(itemId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.EXERCISE_NOT_FOUND);
        }
        exerciseMapper.deleteById(itemId);
    }

    @Override
    public List<ExerciseBatch> listBatchByChapter(Long chapterId) {
        LambdaQueryWrapper<ExerciseBatch> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(ExerciseBatch::getChapterId, chapterId)
                .orderByDesc(ExerciseBatch::getCreateTime);

        return exerciseBatchMapper.selectList(wrapper);
    }


    @Override
    public List<ExerciseItem> listByBatch(Long chapterId, Long batchId) {
        // 1. 校验batch是否存在且属于该章节
        ExerciseBatch batch = exerciseBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessException(ErrorCode.EXERCISE_BATCH_NOT_FOUND);
        }
        if (!batch.getChapterId().equals(chapterId)) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_BELONG_TO_CLASS);
        }
        // 2. 查该批次下所有题目
        LambdaQueryWrapper<ExerciseItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExerciseItem::getBatchId, batchId);
        return exerciseMapper.selectList(wrapper);
    }


    //答案部分

    @Override
    public TaskCreatedVO uploadAnswer(Long classId, Long chapterId, MultipartFile file) {
        // 1. 校验章节是否存在
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        // 2. 上传文件到OSS
        String ossUrl = ossUtil.upload(file);

        // 3. 先创建AnswerBatch
        // 找到该章节最新的ExerciseBatch
        LambdaQueryWrapper<ExerciseBatch> batchWrapper = new LambdaQueryWrapper<>();
        batchWrapper.eq(ExerciseBatch::getChapterId, chapterId)
                .orderByDesc(ExerciseBatch::getCreateTime)
                .last("LIMIT 1");
        ExerciseBatch exerciseBatch = exerciseBatchMapper.selectOne(batchWrapper);
        if (exerciseBatch == null) {
            throw new BusinessException(ErrorCode.EXERCISE_BATCH_NOT_FOUND);
        }

        AnswerBatch answerBatch = AnswerBatch.builder()
                .exerciseBatchId(exerciseBatch.getId())
                .ossUrl(ossUrl)
                .parseStatus(ParseStatus.PENDING)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        answerBatchMapper.insert(answerBatch);

        Task task = Task.builder()
                .taskType(TaskType.ANSWER_PARSE)
                .status(ParseStatus.PENDING)
                .bizId(answerBatch.getId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);
        aiService.parseAnswerAsync(task.getTaskId(), ossUrl, answerBatch.getId());

        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.ANSWER_PARSE);
        vo.setBizId(answerBatch.getId());
        return vo;
    }

    @Override
    public AnswerItem getAnswerPreview(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        if (task.getStatus() != ParseStatus.DONE) {
            throw new BusinessException(ErrorCode.TASK_NOT_DONE);
        }
        return aiService.getAnswerPreview(taskId);
    }

    @Override
    public AnswerBatchResultVO confirmAnswer(AnswerConfirmDTO dto) {
        AnswerBatch answerBatch = answerBatchMapper.selectById(dto.getAnswerBatchId());
        if (answerBatch == null) {
            throw new BusinessException(ErrorCode.ANSWER_BATCH_NOT_FOUND);
        }

        int successCount = 0;
        int failCount = 0;

        for (AnswerItem item : dto.getAnswers()) {
            try {
                ExerciseItem exerciseItem = exerciseMapper.selectById(item.getExerciseItemId());
                if (exerciseItem == null) {
                    throw new BusinessException(ErrorCode.EXERCISE_NOT_FOUND);
                }
                item.setAnswerBatchId(dto.getAnswerBatchId());
                item.setId(null);
                answerMapper.insert(item);
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        answerBatch.setParseStatus(ParseStatus.DONE);
        answerBatch.setUpdateTime(LocalDateTime.now());
        answerBatchMapper.updateById(answerBatch);

        // 清除Redis缓存
        redisUtil.delete("answer:preview:" + dto.getTaskId());
        AnswerBatchResultVO vo = new AnswerBatchResultVO();
        vo.setBatchId(dto.getAnswerBatchId());
        vo.setSuccessCount(successCount);
        vo.setFailCount(failCount);
        return vo;
    }

    @Override
    public void updateAnswer(Long itemId, AnswerItem item) {
        // 1. 校验是否存在
        AnswerItem existing = answerMapper.selectById(itemId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.ANSWER_NOT_FOUND);
        }
        // 2. 校验是否已批改，批改后不能修改
        LambdaQueryWrapper<Task> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(Task::getBizId, existing.getAnswerBatchId())
                .eq(Task::getTaskType, TaskType.GRADE);
        Task gradeTask = taskMapper.selectOne(taskWrapper);
        if (gradeTask != null && gradeTask.getStatus() != ParseStatus.PENDING) {
            throw new BusinessException(ErrorCode.ANSWER_ALREADY_GRADED);
        }
        // 3. 只允许修改答案和解析
        existing.setAnswer(item.getAnswer());
        existing.setExplanation(item.getExplanation());
        answerMapper.updateById(existing);
    }

    @Override
    public void deleteAnswer(Long answerBatchId) {
        // 1. 校验是否存在
        AnswerBatch answerBatch = answerBatchMapper.selectById(answerBatchId);
        if (answerBatch == null) {
            throw new BusinessException(ErrorCode.ANSWER_BATCH_NOT_FOUND);
        }
        // 2. 校验是否已批改
        LambdaQueryWrapper<Task> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(Task::getBizId, answerBatchId)
                .eq(Task::getTaskType, TaskType.GRADE);
        Task gradeTask = taskMapper.selectOne(taskWrapper);
        if (gradeTask != null && gradeTask.getStatus() == ParseStatus.DONE) {
            throw new BusinessException(ErrorCode.ANSWER_ALREADY_GRADED);
        }
        // 3. 删除OSS文件
        ossUtil.delete(answerBatch.getOssUrl());
        // 4. 删除所有AnswerItem
        LambdaQueryWrapper<AnswerItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(AnswerItem::getAnswerBatchId, answerBatchId);
        answerMapper.delete(itemWrapper);
        // 5. 删除AnswerBatch
        answerBatchMapper.deleteById(answerBatchId);
    }




    @Override
    public TaskCreatedVO triggerAutoGrade(Long exerciseId) {
        ExerciseItem exercise = exerciseMapper.selectById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.EXERCISE_NOT_FOUND);
        }
        if (exercise.getAnswer() == null) {
            throw new BusinessException(ErrorCode.ANSWER_NOT_FOUND);
        }
        Task task = Task.builder()
                .taskType(TaskType.GRADE)
                .status(ParseStatus.PENDING)
                .bizId(exerciseId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        taskMapper.insert(task);
        aiService.gradeAsync(task.getTaskId(), exerciseId);
        TaskCreatedVO vo = new TaskCreatedVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskType(TaskType.GRADE);
        vo.setBizId(exerciseId);
        return vo;
    }

    @Override
    public GradeTaskStatusVO getGradeTaskStatus(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        GradeTaskStatusVO vo = new GradeTaskStatusVO();
        vo.setTaskId(task.getTaskId());
        vo.setStatus(task.getStatus());
        vo.setFailReason(task.getFailReason());
        return vo;
    }

    @Override
    public ExerciseClassReportVO getClassReport(Long exerciseId) {
        ExerciseItem exercise = exerciseMapper.selectById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.EXERCISE_NOT_FOUND);
        }
        return aiService.getClassReport(exerciseId);
    }



    //TODO:FileType的类型是否要区分ppt和pdf？
    private FileType detectFileType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        switch (ext) {
            case ".pdf":
            case ".ppt":
            case ".pptx":
            case ".doc":
            case ".docx": return FileType.DOCUMENT;
            case ".mp3":
            case ".wav":
            case ".m4a":  return FileType.AUDIO;
            case ".mp4":
            case ".mov":  return FileType.VIDEO;
            default: throw new BusinessException(ErrorCode.FILE_TYPE_NOT_SUPPORT);
        }
    }
}
