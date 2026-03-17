package com.example.exception.handle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.exception.MyApiSpecificException;
import com.example.exception.dto.ErrorResponse;

    @RestControllerAdvice(annotations = RestController.class)
    public class GlobalExceptionHandler {
    @ExceptionHandler(MyApiSpecificException.class) 
    public ResponseEntity<ErrorResponse> handleOnlyMyApiError(MyApiSpecificException ex) {
        ErrorResponse body = new ErrorResponse(500, "Internal Server Error", null); 
        
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
