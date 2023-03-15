package com.man.component.exception;

import com.man.common.enums.ResultCode;
import com.man.common.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常捕获类
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> exceptionHandler(Exception ex) {
        log.error("Exception： " + ex.getMessage(), ex);
        return CommonResult.failed(ResultCode.FAILED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> res = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        log.info("参数校验错误: {}", res);
        return CommonResult.validateFailed(res);
    }

    /**
     * 请求参数异常处理
     */
    @ExceptionHandler(ValidationException.class)
    public CommonResult<List<String>> handleMethodArgumentNotValidException(ValidationException e) {
        log.info("参数校验错误: {}", e.getMessage());
        return CommonResult.validateFailed(e.getMessage());
    }
}
