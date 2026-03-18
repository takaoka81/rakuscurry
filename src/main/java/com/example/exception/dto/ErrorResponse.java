package com.example.exception.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

  public class ErrorResponse { 
    private Integer status; 
    private String message; 
  @JsonInclude(JsonInclude.Include.NON_NULL) 
  private Map<String, String> details;

    public ErrorResponse(Integer status, String message, Map<String, 
        String> details) { 
    this.status = status; 
    this.message = message; 
    this.details = details; 
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    } 

    
}
