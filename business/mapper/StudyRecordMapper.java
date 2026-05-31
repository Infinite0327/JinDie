package kdec.apple.cloud.app.business.mapper;

import kdec.apple.cloud.app.common.entity.StudyRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudyRecordMapper {
    private static final List<StudyRecord> RECORD_LIST = new ArrayList<>();

    static {
        LocalDate today = LocalDate.now();
        RECORD_LIST.add(new StudyRecord(1L, 10001L, 1L, 45, today.minusDays(2)));
        RECORD_LIST.add(new StudyRecord(2L, 10001L, 1L, 35, today.minusDays(1)));
        RECORD_LIST.add(new StudyRecord(3L, 10001L, 2L, 50, today));
        RECORD_LIST.add(new StudyRecord(4L, 10002L, 1L, 30, today.minusDays(1)));
        RECORD_LIST.add(new StudyRecord(5L, 10002L, 2L, 40, today));
    }

    public List<StudyRecord> selectByStudentId(Long studentId) {
        List<StudyRecord> result = new ArrayList<>();
        for (StudyRecord record : RECORD_LIST) {
            if (studentId == null || studentId.equals(record.getStudentId())) {
                result.add(record);
            }
        }
        return result;
    }

    public List<StudyRecord> selectByStudentIds(List<Long> studentIds) {
        List<StudyRecord> result = new ArrayList<>();
        for (StudyRecord record : RECORD_LIST) {
            if (studentIds == null || studentIds.contains(record.getStudentId())) {
                result.add(record);
            }
        }
        return result;
    }

    public void insert(StudyRecord record) {
        RECORD_LIST.add(record);
    }
}
