package com.dailycodebuffer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
@Getter
public class ValidateException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private static String DEFAULT_MESSAGE  = "NOT FOUND";
	private String message;
	private String status;
	
	public ValidateException(String message, String status){
		super(message);
		this.message = message;
		this.status = status;
	}
	
	public ValidateException(String message){
		super(message);
		this.message = message;
		this.status = String.valueOf(HttpStatus.NOT_FOUND.value());;
	}
	
	public ValidateException(){
		super();
		this.message = DEFAULT_MESSAGE;
		this.status = String.valueOf(HttpStatus.NOT_FOUND.value());
	}
	
}
