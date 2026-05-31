package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.ExerciseRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciseRecordMapper {
    private static final List<ExerciseRecord> RECORD_LIST = new ArrayList<>();

    static {
        RECORD_LIST.add(new ExerciseRecord(
                1L, 10001L, 1001L, 1L, "[\"B\"]", true, 1,
                "Correct", LocalDateTime.now().minusDays(2)
        ));
        RECORD_LIST.add(new ExerciseRecord(
                2L, 10001L, 1001L, 2L, "[\"A\"]", false, 0,
                "Review the applicable conditions.", LocalDateTime.now().minusDays(1)
        ));
        RECORD_LIST.add(new ExerciseRecord(
                3L, 10002L, 1001L, 1L, "[\"A\"]", false, 0,
                "Review the core concept.", LocalDateTime.now().minusDays(1)
        ));
        RECORD_LIST.add(new ExerciseRecord(
                4L, 10002L, 2001L, 3L, "[\"true\"]", true, 1,
                "Correct", LocalDateTime.now()
        ));
    }

    public void insert(ExerciseRecord record) {
        RECORD_LIST.add(record);
    }

    public List<ExerciseRecord> selectAll() {
        return RECORD_LIST;
    }

    public List<ExerciseRecord> selectByBatchId(Long batchId) {
        List<ExerciseRecord> result = new ArrayList<>();

        for (ExerciseRecord record : RECORD_LIST) {
            if (batchId == null || batchId.equals(record.getBatchId())) {
                result.add(record);
            }
        }

        return result;
    }

    public List<ExerciseRecord> selectByStudentIdAndBatchId(Long studentId, Long batchId) {
        List<ExerciseRecord> result = new ArrayList<>();

        for (ExerciseRecord record : RECORD_LIST) {
            if (studentId != null && !studentId.equals(record.getStudentId())) {
                continue;
            }

            if (batchId != null && !batchId.equals(record.getBatchId())) {
                continue;
            }

            result.add(record);
        }

        return result;
    }
}
