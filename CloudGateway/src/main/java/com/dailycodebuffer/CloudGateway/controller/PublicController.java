package com.dailycodebuffer.CloudGateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PublicController {

    @GetMapping("livenessProbe")
    public ResponseEntity<String> getOrderDetails() {
        return ResponseEntity.ok(null);
    }
}
