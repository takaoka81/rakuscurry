package com.example.exception;

public class MyApiSpecificException extends RuntimeException {
/**
 * 独自例外
 */
    public MyApiSpecificException(String message){
        super(message);
    }

}
