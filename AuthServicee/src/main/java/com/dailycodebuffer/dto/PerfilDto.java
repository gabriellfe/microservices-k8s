package com.dailycodebuffer.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDto {
	
	private String email;
	private String nome;
	private Date nascimento;
	private String telefone;
	private String genero;
	private String estado;
	private String cidade;
	private String cpf;

}
