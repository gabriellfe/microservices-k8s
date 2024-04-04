package com.dailycodebuffer.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientDTO {
	
	private String email;
	private String nome;
	private Date nascimento;
	private String telefone;
	private String genero;
	private EstadoDto estado;
	private String cidade;
	private String cpf;
	private List<PetDTO> pets;
	

}
