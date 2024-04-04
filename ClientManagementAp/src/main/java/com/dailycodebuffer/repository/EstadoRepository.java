package com.dailycodebuffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailycodebuffer.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long>{
	
	public Estado findBySigla(String sigla);

}
