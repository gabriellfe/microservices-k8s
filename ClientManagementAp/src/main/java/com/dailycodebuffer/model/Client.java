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

    @Column(name = "TELEFONE")
    private String telefone;
    
    @Column(name = "CIDADE")
    private String cidade;
    
    @Column(name = "BAIRRO")
    private String bairro;
    
    @Column(name = "ESTADO")
    private String estado;
    
    @Column(name = "ENDERECO")
    private String endereco;
    
    @Column(name = "DT_CRIACAO")
    private Instant dtCriacao;

}
