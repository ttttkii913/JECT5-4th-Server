package com.sossbar.global.common.exception;

import com.sossbar.global.common.code.ErrorCode;
import com.sossbar.global.common.template.ApiResTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class CustomExceptionAdvice {

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResTemplate<?>> handleServerException(final Exception e) {
        log.error("Internal Server Error: {}", e.getMessage(), e);

        ApiResTemplate<?> body =
                ApiResTemplate.errorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(body);
    }

    // custom error
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResTemplate> handleCustomException(BusinessException e) {
        log.error("CustomException: {}", e.getMessage(), e);

        ApiResTemplate<?> body = ApiResTemplate.errorResponse(e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(body);
    }

    // 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResTemplate<String>> handleValidationExceptions(MethodArgumentNotValidException e) {

        Map<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResTemplate<String> body =
                ApiResTemplate.errorResponse(ErrorCode.VALIDATION_ERROR, convertMapToString(errorMap));

        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
                .body(body);
    }

    private String convertMapToString(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(java.util.stream.Collectors.joining(", "));
    }
}
