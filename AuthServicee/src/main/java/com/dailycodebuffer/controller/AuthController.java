package com.dailycodebuffer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodebuffer.dto.LoginRequestDTO;
import com.dailycodebuffer.dto.UsuarioRequestDTO;
import com.dailycodebuffer.service.AuthService;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authservice;
	
	@PostMapping(value = "/user/login")
	public ResponseEntity<String> doLogin(@RequestBody LoginRequestDTO loginRequestDto){
		return ResponseEntity.ok(authservice.doLogin(loginRequestDto));
	}
	
	@PostMapping(value = "/user/create/user")
	public ResponseEntity<String> createUser(@RequestBody UsuarioRequestDTO loginRequestDto){
		authservice.createUser(loginRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


}
