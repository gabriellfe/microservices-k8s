package com.dailycodebuffer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodebuffer.dto.CreateClientDTO;
import com.dailycodebuffer.service.ClientService;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;
    
    @GetMapping
    public ResponseEntity<?> getClient() {
        return new ResponseEntity<>(clientService.listAll(), HttpStatus.OK);
    }
    
    @GetMapping("/pet/{idCliente}")
    public ResponseEntity<?> getClientPets(@PathVariable(name = "idCliente") Long idCliente) {
        return new ResponseEntity<>(clientService.findPets(idCliente), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> saveClient(@RequestBody CreateClientDTO dto) {
    	clientService.createClient(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
