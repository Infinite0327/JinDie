package kdec.apple.cloud.app.webapi.student;

import kdec.apple.cloud.app.business.service.StudentExerciseService;
import kdec.apple.cloud.app.business.service.impl.StudentExerciseServiceImpl;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseBatchViewDTO;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseItemSearchRequest;
import kdec.apple.cloud.app.common.dto.exercise.ExerciseItemViewDTO;
import kdec.apple.cloud.app.common.dto.exercise.SubmitExerciseItemRequest;
import kdec.apple.cloud.app.common.dto.exercise.SubmitExerciseItemResponse;

import java.util.List;

public class StudentExerciseController {
    private final StudentExerciseService studentExerciseService = new StudentExerciseServiceImpl();

    public List<ExerciseBatchViewDTO> listBatchesByChapterId(Long chapterId) {
        return studentExerciseService.listBatchesByChapterId(chapterId);
    }

    public List<ExerciseItemViewDTO> searchExerciseItems(ExerciseItemSearchRequest request) {
        return studentExerciseService.searchExerciseItems(request);
    }

    public SubmitExerciseItemResponse submitExerciseItem(SubmitExerciseItemRequest request) {
        return studentExerciseService.submitExerciseItem(request);
    }
}