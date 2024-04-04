package com.dailycodebuffer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "RACA")
    private String raca;

    @Column(name = "PELAGEM")
    private String pelagem;
    
    @Column(name = "PORTE")
    private String porte;
    
    @Column(name = "IMAGEM")
    private String imagem;
    
    @Column(name = "PET_HASH")
    private String petHash;
    
    @Column(name = "ES_PERFUME")
    private String esPerfume;
    
    @Column(name = "ES_ENFEITE")
    private String esEnfeite;
    
    @Column(name = "ID_CLIENTE")
    private Long idCliente;
}
