package kdec.apple.base.common.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {

    // 通用
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),


    // 班级
    CLASS_NOT_FOUND(1001, "班级不存在"),
    STUDENT_ALREADY_IN_CLASS(1002, "学生已在班级中"),
    STUDENT_NOT_IN_CLASS(1003, "学生不在该班级中"),
    FILE_PARSE_ERROR(1601, "文件解析失败"),
    FILE_EMPTY(1602, "文件内容为空"),
    CHAPTER_NOT_BELONG_TO_CLASS(1302, "章节不属于该课程"),

    // 学生
    STUDENT_NOT_FOUND(1101, "学生不存在"),

    // 资料
    MATERIAL_NOT_FOUND(1201, "资料不存在"),
    FILE_TYPE_NOT_SUPPORT(1202, "不支持的文件类型"),
    OSS_UPLOAD_FAILED(1701, "文件上传失败"),
    OSS_DELETE_FAILED(1702, "文件删除失败"),

    // 章节
    CHAPTER_NOT_FOUND(1301, "章节不存在"),

    // 习题
    EXERCISE_NOT_FOUND(1401, "习题不存在"),
    ANSWER_ALREADY_GRADED(1402, "已批改完成，无法修改答案"),
    // 习题批次
    EXERCISE_BATCH_NOT_FOUND(1401, "习题批次不存在"),
    //答案
    ANSWER_BATCH_NOT_FOUND(40421, "答案批次不存在"),
    ANSWER_NOT_FOUND(40422, "答案不存在"),

    // 任务
    TASK_NOT_FOUND(1501, "任务不存在"),
    TASK_NOT_DONE(1502, "任务尚未完成"),

    PARSE_RESULT_NOT_FOUND(1801, "解析结果不存在"),
    NOTE_NOT_FOUND(1802, "笔记不存在"),


    ANNOUNCEMENT_NOT_FOUND(40411, "公告不存在"),;

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}