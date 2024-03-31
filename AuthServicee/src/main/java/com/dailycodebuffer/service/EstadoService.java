package com.dailycodebuffer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailycodebuffer.model.Estado;
import com.dailycodebuffer.repository.EstadoRepository;


@Service
public class EstadoService {
	
	@Autowired
	private EstadoRepository repository;
	
	public List<Estado> getEstados() {
		return repository.findAll();
	}

	public Estado findBySigla(String estado) {
		return repository.findBySigla(estado);
	}
}
