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
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long idClient;

    @Column(name = "NOME")
    private String nome;

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

    @Column(name = "DT_CRIACAO")
    private Instant dtCriacao;

}
