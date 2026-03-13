package com.example.exception.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.exception.dto.ErrorResponse;

    @RestControllerAdvice
    public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class) 
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse body = new ErrorResponse(500, "Internal Server Error", null); 
        
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
