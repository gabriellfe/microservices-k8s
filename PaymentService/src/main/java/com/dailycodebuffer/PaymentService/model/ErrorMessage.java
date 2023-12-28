package com.dailycodebuffer.PaymentService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {
	
	private String message;
	private String status;

}
