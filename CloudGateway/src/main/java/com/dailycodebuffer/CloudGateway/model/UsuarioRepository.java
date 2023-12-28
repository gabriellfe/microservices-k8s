package com.dailycodebuffer.CloudGateway.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{

	public Usuario findByEmail(String Email);

}
