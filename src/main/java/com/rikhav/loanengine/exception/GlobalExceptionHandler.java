package com.rikhav.loanengine.exception;

import com.rikhav.loanengine.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .toList();
        return badRequest("Validation failed", messages);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return badRequest(
                "Malformed request",
                List.of("Request body is missing, is not valid JSON, or contains an invalid value "
                        + "(for example an unknown employmentType or purpose)"));
    }

    private ResponseEntity<ErrorResponse> badRequest(String error, List<String> messages) {
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), error, messages);
        return ResponseEntity.badRequest().body(body);
    }
}