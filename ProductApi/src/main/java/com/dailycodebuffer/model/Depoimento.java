package com.dailycodebuffer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "DEPOIMENTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Depoimento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "TEXTO")
    private String texto;

    @Column(name = "AUTOR")
    private String autor;

    @Column(name = "AVATAR")
    private String avatar;
}
