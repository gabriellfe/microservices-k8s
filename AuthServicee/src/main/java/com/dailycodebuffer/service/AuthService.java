package com.dailycodebuffer.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.commons.dto.UserDTO;
import com.dailycodebuffer.commons.service.RedisService;
import com.dailycodebuffer.dto.LoginRequestDTO;
import com.dailycodebuffer.dto.UsuarioRequestDTO;
import com.dailycodebuffer.exception.AuthLoginException;
import com.dailycodebuffer.model.Usuario;
import com.dailycodebuffer.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		return this.generateToken(user);
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

	public void refreshLogin(String token) {
		try {
			if (!this.validateAuthorization(token)) {
				throw new AuthLoginException("Token invalid");
			}
			this.generateToken(usuarioRepository.findByEmail(this.decode(token).getClient()));
		} catch (Exception e) {
			throw new AuthLoginException("Token invalid");
		}
	}
	private boolean validateAuthorization(String jwtToken) {
		try {
			UserDTO user = this.decode(jwtToken);
			log.info("Client: [{}]", user.getClient());
			log.info("Secret: [{}]", redisService.getValue("AUTH_" + user.getClient()));
			String secret = (String) redisService.getValue("AUTH_" + user.getClient());
			JWT.require(Algorithm.HMAC256(secret)).build().verify(jwtToken);
			return true;
		} catch (Exception e) {
			log.error("Error: {}", e);
		}
		return false;
	}
	
	private String generateToken(Usuario user){
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
	
	private UserDTO decode(String jwtToken) throws Exception {
		log.info("Token: [{}]", jwtToken);
		String base64EncodedBody = jwtToken.split("\\.")[1];
		String body = new String(Base64.getDecoder().decode(base64EncodedBody));
		ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper.readValue(body, UserDTO.class);
	}
}
