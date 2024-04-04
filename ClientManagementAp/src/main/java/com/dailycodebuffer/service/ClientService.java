package com.dailycodebuffer.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.dailycodebuffer.dto.CreateClientDTO;
import com.dailycodebuffer.model.Client;
import com.dailycodebuffer.model.Pet;
import com.dailycodebuffer.repository.ClientRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ClientService{

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private PetService petService;


	public List<Client> listAll() {
		return this.clientRepository.findAll();
	}
	
	public Client findById(Long id) {
		return this.clientRepository.findById(id).orElseThrow(() -> new NotFoundException("Client Not found"));
	}
	
	public void createClient(CreateClientDTO dto) {
		Client client = new Client();
		client.setDtCriacao(Instant.now());
		client.setEmail(dto.getEmail());
		client.setNome(dto.getNome());
		client.setCidade(dto.getCidade());
		client.setCpf(dto.getCpf());
		client.setEstado(dto.getEstado().getSigla());
		client.setGenero(dto.getGenero());
		client.setTelefone(dto.getTelefone());
		client.setNascimento(dto.getNascimento());
		client = clientRepository.save(client);
		long idCliente = client.getIdClient();
		dto.getPets().stream().forEach(i -> {
			Pet pet = new Pet();
			pet.setIdCliente(idCliente);
			pet.setNome(i.getNomePet());
			pet.setEsEnfeite(i.getEsEnfeite().toString());
			pet.setEsPerfume(i.getEsEnfeite().toString());
			pet.setPelagem(i.getPelagem());
			pet.setPetHash(i.getPetHash());
			pet.setPorte(i.getPorte());
			pet.setRaca(i.getRaca());
			petService.savePet(pet);
		});

	}

	public List<Pet> findPets(Long idCliente) {
		return null;
	}
}
