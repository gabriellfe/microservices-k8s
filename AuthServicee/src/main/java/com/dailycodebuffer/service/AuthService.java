package com.dailycodebuffer.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.commons.dto.UserDTO;
import com.dailycodebuffer.commons.service.RedisService;
import com.dailycodebuffer.dto.EditaPerfilDto;
import com.dailycodebuffer.dto.EstadoDto;
import com.dailycodebuffer.dto.GenerateTicketDto;
import com.dailycodebuffer.dto.LoginRequestDTO;
import com.dailycodebuffer.dto.PerfilDto;
import com.dailycodebuffer.dto.TicketDto;
import com.dailycodebuffer.dto.TokenDto;
import com.dailycodebuffer.dto.UsuarioRequestDTO;
import com.dailycodebuffer.exception.AuthLoginException;
import com.dailycodebuffer.model.Estado;
import com.dailycodebuffer.model.TicketRedefinicao;
import com.dailycodebuffer.model.Usuario;
import com.dailycodebuffer.repository.TicketRedefinicaoRepository;
import com.dailycodebuffer.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
	
	@Value("enable.redis")
	private boolean redisEnable;

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TicketRedefinicaoRepository ticketRedefinicaoRepository;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private EstadoService estadoService;

	public TokenDto doLogin(LoginRequestDTO login) {
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
		return new TokenDto(this.generateToken(user));
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
		user.setName(login.getNome());
		user.setCidade(login.getCidade());
		user.setCpf(login.getCpf());
		user.setEstado(login.getEstado().getSigla());
		user.setGenero(login.getGenero());
		user.setTelefone(login.getTelefone());
		user.setNascimento(login.getNascimento());
		user.setPassword(bcrypt.encode(login.getSenha()));
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

	public TokenDto refreshLogin(String token) {
		try {
			if (!this.validateAuthorization(token)) {
				throw new AuthLoginException("Token invalid");
			}
			String jwtToken = this.generateToken(usuarioRepository.findByEmail(this.decode(token).getClient()));
			return new TokenDto(jwtToken);
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
		if (redisEnable){
			redisService.setValue("AUTH_" + user.getEmail(), secretKey, TimeUnit.MINUTES, 30, false);
		} else {
			user.setSecret(secretKey);
			usuarioRepository.save(user);
		}
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
	
	public Long geraCodigoRedefinicao(GenerateTicketDto generateTicketDto){
		Usuario user = usuarioRepository.findByEmail(generateTicketDto.getEmail());
		TicketRedefinicao ticket = new TicketRedefinicao();
		ticket.setDtCriacao(Instant.now());
		ticket.setIdUsuario(user.getId());
		ticket.setTicket(this.generateTicket());
		ticket.setEsValido("S");
		ticket = ticketRedefinicaoRepository.save(ticket);
		return ticket.getTicket();
	}
	
	private Long generateTicket() {
		Random random = new Random();
		char[] digits = new char[6];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < 6; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return Long.parseLong(new String(digits));
	}

	public void logout(String token) throws Exception {
		if (token != null) {
			UserDTO user = decode(token);
			redisService.removeKey("AUTH_" + user.getClient());
		}
	}

	public void changePassword(TicketDto ticketDto) {
		Usuario user = this.validateTicket(ticketDto.getEmail(), ticketDto.getTicket());
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		user.setPassword(bcrypt.encode(ticketDto.getPassword()));
		usuarioRepository.save(user);
	}

	private Usuario validateTicket(String email, Long ticket) {
		Usuario user = usuarioRepository.findByEmail(email);
		if (user == null)
			throw new AuthLoginException();
		
		TicketRedefinicao ticketRedefinicao = ticketRedefinicaoRepository.findByIdUsuarioAndTicket(user.getId(), ticket);
		
		if (ticketRedefinicao == null)
			throw new AuthLoginException();
		
		if (ticketRedefinicao.getEsValido().equals("N"))
			throw new AuthLoginException();
		ticketRedefinicao.setEsValido("N");
		ticketRedefinicaoRepository.save(ticketRedefinicao);
		return user;
	}

	public PerfilDto getPerfil(String token) throws Exception {
		Usuario user = usuarioRepository.findByEmail(this.decode(token).getClient());
		Estado estado = estadoService.findBySigla(user.getEstado());
		PerfilDto response = new PerfilDto();
		response.setNome(user.getName());
		response.setCidade(user.getCidade());
		response.setCpf(user.getCpf());
		response.setEmail(user.getEmail());
		response.setGenero(user.getGenero());
		response.setNascimento(user.getNascimento());
		response.setTelefone(user.getTelefone());
		EstadoDto dtoEstado = new EstadoDto(estado.getId(), estado.getSigla(), estado.getNome());
		response.setEstado(dtoEstado);
		return response;
	}

	public void editaPerfil(EditaPerfilDto perfil, String token) throws Exception {
		String email = this.decode(token).getClient();
		Usuario user = usuarioRepository.findByEmail(email);
		
		if (!user.getEmail().equals(perfil.getEmail()) && usuarioRepository.findByEmail(perfil.getEmail()) != null) {
			throw new AuthLoginException();
		}
		user.setName(perfil.getNome());
		user.setCidade(perfil.getCidade());
		user.setCpf(perfil.getCpf());
		user.setEmail(perfil.getEmail());
		user.setEstado(perfil.getEstado().getSigla());
		user.setGenero(perfil.getGenero());
		user.setNascimento(perfil.getNascimento());
		user.setTelefone(perfil.getTelefone());
		usuarioRepository.save(user);
	}

	public List<Usuario> getClients() {
		return usuarioRepository.findAll();
		
	}
}
