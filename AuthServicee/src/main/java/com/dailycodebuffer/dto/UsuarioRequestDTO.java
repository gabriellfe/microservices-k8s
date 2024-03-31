package com.dailycodebuffer.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequestDTO {
	
	private String email;
	private String nome;
	private String senha;
	private Date nascimento;
	private String telefone;
	private String genero;
	private EstadoDto estado;
	private String cidade;
	private String cpf;
	

}
