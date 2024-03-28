package com.dailycodebuffer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.dailycodebuffer.dto.ErrorMessage;

@RestControllerAdvice
public class WebRestExceptionAdvice {

	@ExceptionHandler(value = ValidateException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ErrorMessage resourceAuthLoginException(ValidateException ex, WebRequest request) {
		ErrorMessage message = new ErrorMessage(ex.getMessage(), ex.getStatus());
		return message;
	}
	
	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage resourceException(Exception ex, WebRequest request) {
		ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.name(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
		return message;
	}
}
