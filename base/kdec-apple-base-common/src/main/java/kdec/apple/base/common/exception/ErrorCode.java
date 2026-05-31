package kdec.apple.base.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PARAM_ERROR(400, "Invalid parameter"),
    UNAUTHORIZED(401, "Login required or expired"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    CLASS_NOT_FOUND(1001, "Class not found"),
    STUDENT_ALREADY_IN_CLASS(1002, "Student already in class"),
    STUDENT_NOT_IN_CLASS(1003, "Student not in class"),
    STUDENT_NOT_FOUND(1101, "Student not found"),
    MATERIAL_NOT_FOUND(1201, "Material not found"),
    FILE_TYPE_NOT_SUPPORT(1202, "Unsupported file type"),
    CHAPTER_NOT_FOUND(1301, "Chapter not found"),
    CHAPTER_NOT_BELONG_TO_CLASS(1302, "Chapter does not belong to class"),
    EXERCISE_NOT_FOUND(1401, "Exercise not found"),
    EXERCISE_BATCH_NOT_FOUND(1402, "Exercise batch not found"),
    ANSWER_ALREADY_GRADED(1403, "Answer already graded"),
    TASK_NOT_FOUND(1501, "Task not found"),
    TASK_NOT_DONE(1502, "Task not completed"),
    FILE_PARSE_ERROR(1601, "File parse failed"),
    FILE_EMPTY(1602, "File is empty"),
    OSS_UPLOAD_FAILED(1701, "Upload failed"),
    OSS_DELETE_FAILED(1702, "Delete failed"),
    PARSE_RESULT_NOT_FOUND(1801, "Parse result not found"),
    NOTE_NOT_FOUND(1802, "Note not found"),
    ANSWER_BATCH_NOT_FOUND(40421, "Answer batch not found"),
    ANSWER_NOT_FOUND(40422, "Answer not found"),
    ANNOUNCEMENT_NOT_FOUND(40411, "Announcement not found");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
