package top.aetheria.travelguideplatform.common.exception;

import top.aetheria.travelguideplatform.common.vo.Result;
import top.aetheria.travelguideplatform.common.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace(); // 打印异常堆栈信息
        return Result.error(500, "服务器内部错误");
    }
}