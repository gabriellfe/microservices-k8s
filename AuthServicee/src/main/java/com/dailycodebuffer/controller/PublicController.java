package com.dailycodebuffer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodebuffer.service.EstadoService;

@RestController
@RequestMapping("/")
public class PublicController {
	
	@Autowired
	private EstadoService estadoService;
	
    @GetMapping("livenessProbe")
    public ResponseEntity<?> livenessProbe() {
        return ResponseEntity.ok(true); 
    }
    
    @GetMapping("/estados")
    public ResponseEntity<?> getEstados() {
        return ResponseEntity.ok(estadoService.getEstados()); 
    }
}
