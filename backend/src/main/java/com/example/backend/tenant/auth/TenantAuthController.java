package com.example.backend.tenant.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/auth")
public class TenantAuthController {

    private final TenantAuthService service;

    public TenantAuthController(TenantAuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody TenantAuthRequestDTO data) {
        try {
            return ResponseEntity.ok(service.login(data));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }
    }
}
