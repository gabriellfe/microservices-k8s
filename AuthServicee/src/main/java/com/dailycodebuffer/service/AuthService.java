package com.dailycodebuffer.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.commons.service.RedisService;
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
	
	@Autowired
	private RedisService redisService;

	public String doLogin(LoginRequestDTO login) {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

		Usuario user = usuarioRepository.findByEmail(login.getLogin());
		if (user == null) {
			log.error("User not found");
			throw new AuthLoginException("User not found");
		}
		if (!bcrypt.matches(login.getPassword(), user.getPassword())) {
			log.error("Password Dont match");
			throw new AuthLoginException("Password do not match");
		}
		String currentNanoTime = String.valueOf(System.nanoTime());
		String transactionId = String.valueOf(Thread.currentThread().getId());
		String transaction = currentNanoTime + transactionId;
		String secretKey = Base64.getEncoder()
				.encodeToString(Hashing.sha256().hashString(transaction, StandardCharsets.UTF_8).asBytes());
		JWTCreator.Builder jwtBuilder = JWT.create();
		Algorithm algo = Algorithm.HMAC256(secretKey);
		long nowsec = Calendar.getInstance().getTime().getTime() / 1000;
		redisService.setValue("AUTH_" + user.getEmail(), secretKey, TimeUnit.MINUTES, 30, false);
		log.info("Sucess on client login [{}]", user.getEmail());
		return jwtBuilder.withClaim("EXP", nowsec + 100000).withClaim("client", user.getEmail()).sign(algo);
	}

	public void createUser(UsuarioRequestDTO login) {
		
		if (login.getEmail() == null) {
			log.error("E-mail nao pode ser nulo");
			throw new AuthLoginException("E-mail nao pode ser nulo");
		}

		if (!isValidEmailAddress(login.getEmail())) {
			log.error("Invalid e-mail");
			throw new AuthLoginException("Invalid e-mail");
		}

		Usuario user = usuarioRepository.findByEmail(login.getEmail());
		if (user != null) {
			log.error("User already exists");
			throw new AuthLoginException("User already exists");
		}

		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		user = new Usuario();
		user.setDtCriacao(Instant.now());
		user.setEmail(login.getEmail());
		user.setName(login.getName());
		user.setPassword(bcrypt.encode(login.getPassword()));
		usuarioRepository.save(user);
	}

	public boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

}
