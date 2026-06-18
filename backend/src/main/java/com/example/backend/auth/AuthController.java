package com.example.backend.auth;

import com.example.backend.master.auth.AuthRequestDTO;
import com.example.backend.master.auth.AuthResponseDTO;
import com.example.backend.master.auth.MasterAuthService;
import com.example.backend.security.AuthCookieService;
import com.example.backend.security.SecurityUserPrincipal;
import com.example.backend.tenant.auth.TenantAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MasterAuthService service;
    private final TenantAuthService tenantAuthService;
    private final AuthCookieService authCookieService;

    public AuthController(
            MasterAuthService service,
            TenantAuthService tenantAuthService,
            AuthCookieService authCookieService
    ) {
        this.service = service;
        this.tenantAuthService = tenantAuthService;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDTO login(
            @RequestBody AuthRequestDTO data,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AuthResponseDTO auth = service.login(data);
        authCookieService.attachAuthCookie(response, auth.token(), request);
        return auth;
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public AuthSessionResponseDTO me(Authentication authentication) {
        SecurityUserPrincipal principal = (SecurityUserPrincipal) authentication.getPrincipal();

        if ("tenant".equalsIgnoreCase(principal.getScope())) {
            return tenantAuthService.me(principal);
        }

        return service.me(principal);
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

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authCookieService.clearAuthCookie(response, request);
    }
}
