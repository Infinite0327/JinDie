package kdec.apple.cloud.app.common.dto.enums;

public enum TaskType {
    NOTE,                // AI生成笔记/摘要
    GRAPH,               // AI生成知识图谱
    AUDIO,               // 语音解析
    VIDEO,               // 视频解析
    EXERCISE_PARSE,      // 习题文件解析
    ANSWER_PARSE,        // 答案文件解析
    GRADE,               // AI批改
    TEACHING_SUGGESTION  // AI教学建议
}