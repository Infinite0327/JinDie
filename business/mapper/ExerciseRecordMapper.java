package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.ExerciseRecord;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRecordMapper {
    private static final List<ExerciseRecord> RECORD_LIST = new ArrayList<>();

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