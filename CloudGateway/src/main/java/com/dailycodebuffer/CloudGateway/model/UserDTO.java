package com.dailycodebuffer.CloudGateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	
	@JsonProperty("EXP")
	private Long EXP;
	@JsonProperty("client")
	private String client;

}
