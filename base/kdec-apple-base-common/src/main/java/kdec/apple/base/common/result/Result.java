package kdec.apple.base.common.result;

import lombok.Data;

@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(0, "ok", null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    // 常用错误码快捷方法
    public static <T> Result<T> badRequest(String message) {
        return fail(40001, message);
    }

    public static <T> Result<T> unauthorized() {
        return fail(40101, "未登录或 token 已过期");
    }

    public static <T> Result<T> forbidden() {
        return fail(40301, "无权限");
    }

    public static <T> Result<T> notFound(String message) {
        return fail(40401, message);
    }

    public static <T> Result<T> serverError(String message) {
        return fail(50001, message);
    }
}