package kdec.apple.base.common.exception;


import kdec.apple.base.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(
            BusinessException e
    ) {

        log.warn("业务异常: {}", e.getMessage());

        return Result.fail(
                e.getCode(),
                e.getMessage()
        );
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {

        log.error("系统异常", e);

        return Result.fail(
                ErrorCode.SYSTEM_ERROR.getCode(),
                ErrorCode.SYSTEM_ERROR.getMessage()
        );
    }
}