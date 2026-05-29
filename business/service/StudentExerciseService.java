package kdec.apple.cloud.app.business.service;

import kdec.apple.cloud.app.business.service.StudentExerciseService;
import kdec.apple.cloud.app.business.service.impl.StudentExerciseServiceImpl;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseBatchViewDTO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseItemSearchRequest;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseItemViewDTO;
import kdec.apple.cloud.app.common.dto.exercise.SubmitExerciseItemRequest;
import kdec.apple.cloud.app.common.dto.exercise.SubmitExerciseItemResponse;

import java.util.List;

public interface StudentExerciseService {
    List<ExerciseBatchViewDTO> listBatchesByChapterId(Long chapterId);

    List<ExerciseItemViewDTO> searchExerciseItems(ExerciseItemSearchRequest request);

    SubmitExerciseItemResponse submitExerciseItem(SubmitExerciseItemRequest request);
}