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

    public static <T> Result<T> badRequest(String message) {
        return fail(40001, message);
    }

    public static <T> Result<T> unauthorized() {
        return fail(40101, "Login required or token expired");
    }

    public static <T> Result<T> forbidden() {
        return fail(40301, "Forbidden");
    }

    public static <T> Result<T> notFound(String message) {
        return fail(40401, message);
    }

    public static <T> Result<T> serverError(String message) {
        return fail(50001, message);
    }
}
