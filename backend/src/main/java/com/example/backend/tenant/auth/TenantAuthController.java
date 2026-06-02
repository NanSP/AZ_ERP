package com.example.backend.tenant.auth;

import com.example.backend.auth.ChangePasswordRequestDTO;
import com.example.backend.auth.PasswordChangeResponseDTO;
import com.example.backend.security.SecurityUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenant/auth")
public class TenantAuthController {

    private final TenantAuthService service;

    public TenantAuthController(TenantAuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public TenantAuthResponseDTO login(@RequestBody TenantAuthRequestDTO data) {
        return service.login(data);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public PasswordChangeResponseDTO changePassword(
            @RequestBody ChangePasswordRequestDTO data,
            Authentication authentication
    ) {
        SecurityUserPrincipal principal = (SecurityUserPrincipal) authentication.getPrincipal();
        return service.changePassword(principal, data);
    }
}
