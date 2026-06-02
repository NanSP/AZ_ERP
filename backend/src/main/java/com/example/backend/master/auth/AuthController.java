package com.example.backend.master.auth;

import com.example.backend.auth.ChangePasswordRequestDTO;
import com.example.backend.auth.PasswordChangeResponseDTO;
import com.example.backend.security.SecurityUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MasterAuthService service;

    public AuthController(MasterAuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDTO login(@RequestBody AuthRequestDTO data) {
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
