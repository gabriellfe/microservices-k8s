package com.dailycodebuffer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailycodebuffer.model.Pet;
import com.dailycodebuffer.repository.PetRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PetService{

    @Autowired
    private PetRepository petRepository;

	public List<Pet> listAll() {
		return this.petRepository.findAll();
	}
	
	public List<Pet> listAllByClient(Long idCliente) {
		return this.petRepository.findAllById(idCliente);
	}
	
	public void savePet(Pet pet) {
		this.petRepository.save(pet);
	}

	public List<Pet> findPetsByCliente(Long idCliente) {
		return petRepository.findAllByIdCliente(idCliente);
	}
}
