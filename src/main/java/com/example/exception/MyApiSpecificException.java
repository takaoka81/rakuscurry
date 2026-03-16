package com.example.exception;

public class MyApiSpecificException extends RuntimeException {

    public MyApiSpecificException(String message){
        super(message);
    }

}
