package com.man.component.exception;

import com.man.common.enums.ResultCode;
import com.man.common.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<List<String>> bindExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("参数校验错误");
        List<String> res = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError -> ObjectError.getDefaultMessage())
                .collect(Collectors.toList());
        return CommonResult.validateFailed(res);
    }
}
