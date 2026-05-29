package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.ExerciseBatch;
import kdec.apple.cloud.app.common.enums.ParseStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciseBatchMapper {
    private static final List<ExerciseBatch> BATCH_LIST = new ArrayList<>();

    static {
        BATCH_LIST.add(new ExerciseBatch(
                1001L,
                1L,
                9001L,
                ParseStatus.SUCCESS,
                "oss://exercise/chapter1_batch1.pdf",
                2,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        BATCH_LIST.add(new ExerciseBatch(
                2001L,
                2L,
                9002L,
                ParseStatus.SUCCESS,
                "oss://exercise/chapter2_batch1.pdf",
                1,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
    }

    public List<ExerciseBatch> selectByChapterId(Long chapterId) {
        List<ExerciseBatch> result = new ArrayList<>();

        for (ExerciseBatch batch : BATCH_LIST) {
            if (chapterId == null || chapterId.equals(batch.getChapterId())) {
                result.add(batch);
            }
        }

        return result;
    }

    public ExerciseBatch selectById(Long batchId) {
        if (batchId == null) {
            return null;
        }

        for (ExerciseBatch batch : BATCH_LIST) {
            if (batchId.equals(batch.getId())) {
                return batch;
            }
        }

        return null;
    }
}