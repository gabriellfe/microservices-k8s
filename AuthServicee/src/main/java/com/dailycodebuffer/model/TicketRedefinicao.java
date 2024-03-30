package com.dailycodebuffer.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TICKET_REDEFINICAO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketRedefinicao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Column(name = "TICKET")
    private Long ticket;

    @Column(name = "ID_USUARIO")
    private Long idUsuario;
    
    @Column(name = "DT_CRIACAO")
    private Instant dtCriacao;
    
    @Column(name = "ES_VALIDO")
    private String esValido;

}

