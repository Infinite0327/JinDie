package kdec.apple.cloud.app.business.service.impl;

import kdec.apple.cloud.app.business.mapper.StudyRecordMapper;
import kdec.apple.cloud.app.business.service.StudyRecordService;
import kdec.apple.cloud.app.common.context.UserContext;
import kdec.apple.cloud.app.common.dto.studentCourse.StudyRecordDTO;
import kdec.apple.cloud.app.entity.StudyRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudyRecordServiceImpl implements StudyRecordService {
    private final StudyRecordMapper studyRecordMapper;

    @Override
    public void record(Long courseId, StudyRecordDTO dto) {
        if (dto == null || dto.getDurationMinutes() == null || dto.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("durationMinutes must be greater than zero");
        }
        Long studentId = UserContext.getCurrentUserId();
        if (studentId == null) {
            throw new IllegalStateException("current student is required");
        }
        StudyRecord record = StudyRecord.builder()
                .studentId(studentId)
                .courseId(courseId)
                .chapterId(dto.getChapterId())
                .durationMinutes(dto.getDurationMinutes())
                .studyDate(LocalDate.now())
                .createTime(LocalDateTime.now())
                .build();
        studyRecordMapper.insert(record);
    }
}
