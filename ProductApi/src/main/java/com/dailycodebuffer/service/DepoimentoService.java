package com.dailycodebuffer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailycodebuffer.model.Depoimento;
import com.dailycodebuffer.repository.DepoimentoRepository;

@Service
public class DepoimentoService{
	
	@Autowired
	private DepoimentoRepository depoimentoRepository;

	public List<Depoimento> listAll() {
		return this.depoimentoRepository.findAll();
	}

	public void addDepoimento(Depoimento depoimento) {
		this.depoimentoRepository.save(depoimento);
	}
}
