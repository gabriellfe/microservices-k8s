package com.dailycodebuffer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsePetDto {
	
	private String nomePet;
	private String raca;
	private String pelagem;
	private String porte;
	private String imagem;
	private Boolean esPerfume = Boolean.FALSE;
	private Boolean esEnfeite = Boolean.FALSE;
}
