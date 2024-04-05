package com.dailycodebuffer.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientDTO {
	
	private String email;
	private String nome;
	private String endereco;
	private String telefone;
	private String bairro;
	private EstadoDto estado;
	private String cidade;
	private List<PetDTO> pets;
	

}
