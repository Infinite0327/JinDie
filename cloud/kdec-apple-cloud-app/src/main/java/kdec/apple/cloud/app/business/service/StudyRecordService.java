package kdec.apple.cloud.app.business.service;

import kdec.apple.cloud.app.common.dto.studentCourse.StudyRecordDTO;

public interface StudyRecordService {
    void record(Long courseId, StudyRecordDTO dto);
}
