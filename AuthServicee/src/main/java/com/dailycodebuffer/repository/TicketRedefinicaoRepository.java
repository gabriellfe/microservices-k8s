package com.dailycodebuffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailycodebuffer.model.TicketRedefinicao;
import com.dailycodebuffer.model.Usuario;

@Repository
public interface TicketRedefinicaoRepository extends JpaRepository<TicketRedefinicao,Long>{

	public TicketRedefinicao findByIdUsuarioAndTicket(Long idUsuario, Long ticket);

}
