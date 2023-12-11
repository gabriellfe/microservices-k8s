package com.dailycodebuffer.CloudGateway.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@Data
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AuthorizeException extends RuntimeException {
	
	private String message;
	private HttpStatus http;
	
    private static final long serialVersionUID = 1L;

    public AuthorizeException(String message, HttpStatus http) {
    	this.message = message;
    	this.http = http;

    }
    public AuthorizeException(Exception e, String message, Long StatusCode) {

    }
}
