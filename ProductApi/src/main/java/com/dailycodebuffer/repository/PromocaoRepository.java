package com.dailycodebuffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailycodebuffer.model.Promocao;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {
}
