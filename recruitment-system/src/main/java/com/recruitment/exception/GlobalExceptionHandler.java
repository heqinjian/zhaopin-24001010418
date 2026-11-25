package com.recruitment.exception;

import com.recruitment.common.Result;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 把 @Valid 校验失败等异常统一收成 Result，前端只需要解析 code / message / data
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求体校验失败（@Valid 不通过时抛出）
     * 把每个字段的错误拼成 "字段名: 提示; 字段名: 提示"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            if (!errorMsg.isEmpty()) {
                errorMsg.append("; ");
            }
            errorMsg.append(error.getField()).append(": ").append(error.getDefaultMessage());
        }
        return Result.error(errorMsg.toString());
    }

    /**
     * 兜底处理其他未捕获的异常，避免把堆栈细节返回给前端
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error("系统异常，请稍后重试");
    }
}
