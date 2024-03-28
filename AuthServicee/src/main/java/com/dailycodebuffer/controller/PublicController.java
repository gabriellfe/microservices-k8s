package com.dailycodebuffer.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodebuffer.commons.service.RedisService;
import com.dailycodebuffer.dto.ErrorMessage;

@RestController
@RequestMapping("/")
public class PublicController {
	
	@Autowired
	private RedisService service;

    @GetMapping("livenessProbe")
    public ResponseEntity<?> getOrderDetails() {
    	ErrorMessage body = new ErrorMessage("teste", null);
    	service.setValue("teste", body, TimeUnit.MINUTES, 30, true);
        return ResponseEntity.ok(service.getValue("teste", ErrorMessage.class)); 
    }
}
