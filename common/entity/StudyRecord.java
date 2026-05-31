package kdec.apple.cloud.app.common.entity;

import java.time.LocalDate;

public class StudyRecord {
    private Long id;
    private Long studentId;
    private Long chapterId;
    private Integer durationMinutes;
    private LocalDate studyDate;

    public StudyRecord() {
    }

    public StudyRecord(Long id, Long studentId, Long chapterId, Integer durationMinutes, LocalDate studyDate) {
        this.id = id;
        this.studentId = studentId;
        this.chapterId = chapterId;
        this.durationMinutes = durationMinutes;
        this.studyDate = studyDate;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDate getStudyDate() {
        return studyDate;
    }
}
