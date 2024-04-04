package com.dailycodebuffer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailycodebuffer.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
	
	List<Pet> findAllById(Long id);
	
	List<Pet> findAllByIdCliente(Long idCliente);
}
