package com.dailycodebuffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailycodebuffer.model.Depoimento;

@Repository
public interface DepoimentoRepository extends JpaRepository<Depoimento, Long> {
}
