package com.dailycodebuffer.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.dto.LoginRequestDTO;
import com.dailycodebuffer.dto.UsuarioRequestDTO;
import com.dailycodebuffer.exception.AuthLoginException;
import com.dailycodebuffer.model.Usuario;
import com.dailycodebuffer.repository.UsuarioRepository;
import com.google.common.hash.Hashing;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public String doLogin(LoginRequestDTO login) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		
		Usuario user = usuarioRepository.findByEmail(login.getLogin());
		if (user == null) {
			log.error("User not found");
			throw new AuthLoginException("User not found");
		}
		if (!bcrypt.matches(login.getPassword(), user.getPassword())){
			log.error("Password Dont match");
			throw new AuthLoginException("Password do not match");
		}
		String currentNanoTime = String.valueOf(System.nanoTime());
		String transactionId = String.valueOf(Thread.currentThread().getId());
		String transaction = currentNanoTime + transactionId;
		String secretKey = Base64.getEncoder().encodeToString(Hashing.sha256().hashString(transaction, StandardCharsets.UTF_8).asBytes());
		JWTCreator.Builder jwtBuilder = JWT.create();
		Algorithm algo = Algorithm.HMAC256(secretKey);
		long nowsec = Calendar.getInstance().getTime().getTime() / 1000;
		user.setSecret(secretKey);
		usuarioRepository.save(user);
		return jwtBuilder.withClaim("EXP", nowsec + 100000).withClaim("client", user.getEmail()).sign(algo);
	}

	public void createUser(UsuarioRequestDTO login) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		Usuario user = new Usuario();
		user.setDtCriacao(Instant.now());
		user.setEmail(login.getEmail());
		user.setName(login.getName());
		user.setPassword(bcrypt.encode(login.getPassword()));
		usuarioRepository.save(user);
	}

}
