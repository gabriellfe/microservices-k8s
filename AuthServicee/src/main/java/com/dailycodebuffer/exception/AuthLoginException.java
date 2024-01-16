package com.dailycodebuffer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
@Getter
public class AuthLoginException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private static String DEFAULT_MESSAGE  = "UNAUTHORIZED";
	private String message;
	private String status;
	
	public AuthLoginException(String message, String status){
		super(message);
		this.message = message;
		this.status = status;
	}
	
	public AuthLoginException(String message){
		super(message);
		this.message = message;
		this.status = String.valueOf(HttpStatus.UNAUTHORIZED.value());;
	}
	
	public AuthLoginException(){
		super();
		this.message = DEFAULT_MESSAGE;
		this.status = String.valueOf(HttpStatus.UNAUTHORIZED.value());
	}
	
}
