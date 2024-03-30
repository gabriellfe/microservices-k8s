package com.dailycodebuffer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodebuffer.dto.GenerateTicketDto;
import com.dailycodebuffer.dto.LoginRequestDTO;
import com.dailycodebuffer.dto.PerfilDto;
import com.dailycodebuffer.dto.ResponseTicketDto;
import com.dailycodebuffer.dto.TicketDto;
import com.dailycodebuffer.dto.TokenDto;
import com.dailycodebuffer.dto.UsuarioRequestDTO;
import com.dailycodebuffer.service.AuthService;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authservice;
	
	@PostMapping(value = "/user/login")
	public ResponseEntity<TokenDto> doLogin(@RequestBody LoginRequestDTO loginRequestDto){
		return ResponseEntity.ok(authservice.doLogin(loginRequestDto));
	}
	
	@PostMapping(value = "/user/create")
	public ResponseEntity<String> createUser(@RequestBody UsuarioRequestDTO loginRequestDto){
		authservice.createUser(loginRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PostMapping(value = "/user/refresh")
	public ResponseEntity<TokenDto> refresh(@RequestHeader(value = "token") String token){
		authservice.refreshLogin(token);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping(value = "/user/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "token") String token) throws Exception{
		authservice.logout(token);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping(value = "/user/ticket")
	public ResponseEntity<?> generateTicket(@RequestBody GenerateTicketDto generateTicketDto){
		authservice.geraCodigoRedefinicao(generateTicketDto);
		Long ticket = authservice.geraCodigoRedefinicao(generateTicketDto);
		return ResponseEntity.ok(new ResponseTicketDto(ticket));
	}
	
	@PostMapping(value = "/user/change-password")
	public ResponseEntity<TokenDto> changePassword(@RequestBody TicketDto ticketDto){
		authservice.changePassword(ticketDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping(value = "/user/perfil")
	public ResponseEntity<PerfilDto> getPerfil(@RequestHeader(value = "token") String token) throws Exception{
		return ResponseEntity.ok(authservice.getPerfil(token));
	}
}
