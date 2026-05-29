package kdec.apple.cloud.app.business.service.impl;

import kdec.apple.cloud.app.business.mapper.ExerciseBatchMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseItemMapper;
import kdec.apple.cloud.app.business.mapper.ExerciseRecordMapper;
import kdec.apple.cloud.app.business.service.StudentExerciseService;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseBatchViewDTO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseItemSearchRequest;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseItemViewDTO;
import kdec.apple.cloud.app.common.dto.exercise.SubmitExerciseItemRequest;
import kdec.apple.cloud.app.common.dto.exercise.SubmitExerciseItemResponse;
import kdec.apple.cloud.app.common.entity.ExerciseBatch;
import kdec.apple.cloud.app.common.entity.ExerciseItem;
import kdec.apple.cloud.app.common.entity.ExerciseRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentExerciseServiceImpl implements StudentExerciseService {
    private final ExerciseBatchMapper exerciseBatchMapper = new ExerciseBatchMapper();
    private final ExerciseItemMapper exerciseItemMapper = new ExerciseItemMapper();
    private final ExerciseRecordMapper exerciseRecordMapper = new ExerciseRecordMapper();

    @Override
    public List<ExerciseBatchViewDTO> listBatchesByChapterId(Long chapterId) {
        List<ExerciseBatch> batches = exerciseBatchMapper.selectByChapterId(chapterId);
        List<ExerciseBatchViewDTO> result = new ArrayList<>();

        for (ExerciseBatch batch : batches) {
            result.add(new ExerciseBatchViewDTO(
                    batch.getId(),
                    batch.getChapterId(),
                    batch.getStatus(),
                    batch.getTotalCount(),
                    batch.getDeadline()
            ));
        }

        return result;
    }

    @Override
    public List<ExerciseItemViewDTO> searchExerciseItems(ExerciseItemSearchRequest request) {
        Long batchId = request == null ? null : request.getBatchId();
        String keyword = request == null ? null : request.getKeyword();
        String type = request == null ? null : request.getType();

        List<ExerciseItem> items = exerciseItemMapper.selectByCondition(batchId, keyword, type);
        List<ExerciseItemViewDTO> result = new ArrayList<>();

        for (ExerciseItem item : items) {
            result.add(new ExerciseItemViewDTO(
                    item.getId(),
                    item.getBatchId(),
                    item.getType().name(),
                    item.getContent(),
                    item.getOptions(),
                    item.getKnowledgeTags()
            ));
        }

        return result;
    }

    @Override
    public SubmitExerciseItemResponse submitExerciseItem(SubmitExerciseItemRequest request) {
        if (request == null || request.getExerciseItemId() == null) {
            return new SubmitExerciseItemResponse(false, "", "提交参数为空", 0, "提交失败：参数为空");
        }

        ExerciseItem item = exerciseItemMapper.selectById(request.getExerciseItemId());

        if (item == null) {
            return new SubmitExerciseItemResponse(false, "", "题目不存在", 0, "提交失败：题目不存在");
        }

        String studentAnswer = request.getStudentAnswer();
        boolean correct = normalize(item.getAnswer()).equals(normalize(studentAnswer));
        int score = correct ? 1 : 0;
        String aiFeedback = correct ? "回答正确，继续保持。" : "回答错误，建议回看解析并复习相关知识点。";

        ExerciseRecord record = new ExerciseRecord(
                System.currentTimeMillis(),
                request.getStudentId(),
                item.getBatchId(),
                item.getId(),
                request.getStudentAnswer(),
                correct,
                score,
                aiFeedback,
                LocalDateTime.now()
        );

        exerciseRecordMapper.insert(record);

        return new SubmitExerciseItemResponse(
                correct,
                item.getAnswer(),
                item.getExplanation(),
                score,
                aiFeedback
        );
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.replace(" ", "").trim().toUpperCase();
    }
}