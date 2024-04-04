package com.dailycodebuffer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dailycodebuffer.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
