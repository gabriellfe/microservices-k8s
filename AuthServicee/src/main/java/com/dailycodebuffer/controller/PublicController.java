package com.dailycodebuffer.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dailycodebuffer.commons.service.S3Service;
import com.dailycodebuffer.dto.ErrorMessage;
import com.dailycodebuffer.service.EstadoService;

@RestController
@RequestMapping("/")
public class PublicController {
	
	@Autowired
	private EstadoService estadoService;
	@Autowired
	private S3Service s3Service;
	
    @GetMapping("livenessProbe")
    public ResponseEntity<?> livenessProbe() {
        return ResponseEntity.ok(true); 
    }
    
    @GetMapping("estados")
    public ResponseEntity<?> getEstados() {
        return ResponseEntity.ok(estadoService.getEstados()); 
    }
    
    @GetMapping("get/file")
    public ResponseEntity<?> getEstados(@RequestParam(value = "path") String path) throws Exception {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	File file = s3Service.downloadObject(path);
    	String mimeType = Files.probeContentType(file.toPath());

        responseHeaders.set("Content-Type", mimeType);

    	return ResponseEntity.ok()
    		      .headers(responseHeaders)
    		      .body(Files.readAllBytes(file.toPath()));
    }
    
    @PostMapping("put/file")
    public ResponseEntity<?> uploadS3(@RequestParam("file") MultipartFile file) throws Exception {
    	s3Service.putObject(file.getBytes(), file.getOriginalFilename(), file.getContentType());
    	return ResponseEntity.ok(null); 
    }
    
    @GetMapping("get/file/base64")
    public ResponseEntity<?> base64(@RequestParam(value = "path") String path) throws Exception {
    	HttpHeaders responseHeaders = new HttpHeaders();
    	File file = s3Service.downloadObject(path);
    	String mimeType = Files.probeContentType(file.toPath());
        ErrorMessage body = new ErrorMessage();
        body.setMessage(Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath())));
        
    	return ResponseEntity.ok()
    		      .headers(responseHeaders)
    		      .body(body);
    }
}
