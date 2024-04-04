package com.dailycodebuffer.model;

import java.time.Instant;
import java.util.Date;

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
@Table(name = "USUARIO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;
    
    @Column(name = "NASCIMENTO")
    private Date nascimento;
    
    @Column(name = "CPF")
    private String cpf;
    
    @Column(name = "TELEFONE")
    private String telefone;
    
    @Column(name = "CIDATE")
    private String cidade;
    
    @Column(name = "ESTADO")
    private String estado;
    
    @Column(name = "GENERO")
    private String genero;
    
    @Column(name = "SECRET")
    private String secret;

    @Column(name = "DT_CRIACAO")
    private Instant dtCriacao;

}

