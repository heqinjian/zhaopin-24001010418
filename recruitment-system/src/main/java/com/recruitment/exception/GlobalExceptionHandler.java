package com.recruitment.exception;

import com.recruitment.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 把 @Valid 校验失败等异常统一收成 Result，前端只需要解析 code / message / data
 */
@Slf4j
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
        // 校验失败属于「请求到了、业务没通过」，记 WARN
        log.warn("参数校验失败: {}", errorMsg);
        return Result.error(errorMsg.toString());
    }

    /**
     * 兜底处理其他未捕获的异常，避免把堆栈细节返回给前端
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 第二个参数传异常本身，堆栈才会进日志；返回给前端的仍然只有一句提示
        log.error("系统异常", e);
        return Result.error("系统异常，请稍后重试");
    }
}
