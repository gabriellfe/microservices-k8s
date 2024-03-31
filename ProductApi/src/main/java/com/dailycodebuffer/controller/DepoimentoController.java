package com.dailycodebuffer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodebuffer.dto.ProductRequest;
import com.dailycodebuffer.dto.ProductResponse;
import com.dailycodebuffer.model.Depoimento;
import com.dailycodebuffer.model.Promocao;
import com.dailycodebuffer.service.DepoimentoService;
import com.dailycodebuffer.service.ProductService;

@RestController
@RequestMapping("/depoimento")
public class DepoimentoController {

    @Autowired
    private DepoimentoService depoimentoService;
    
    @GetMapping
    public ResponseEntity<?> getDepoimento() {
        return new ResponseEntity<>(depoimentoService.listAll(), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> addDepoimento(@RequestBody Depoimento depo) {
    	depoimentoService.addDepoimento(depo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
