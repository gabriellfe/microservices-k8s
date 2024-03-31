package com.dailycodebuffer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "PROMOCAO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "DESTINO")
    private String destino;

    @Column(name = "IMAGEM")
    private String imagem;

    @Column(name = "QUANTITY")
    private BigDecimal preco;
}
