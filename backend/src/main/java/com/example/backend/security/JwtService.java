package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Long userId, String login, String role, String scope) {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret());

        return JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withSubject(login)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withClaim("scope", scope)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(jwtProperties.getExpirationHours(), ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public String generateTenantToken(
            Long tenantId,
            String tenantCode,
            Long userId,
            String login,
            String role
    ) {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret());

        return JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withSubject(login)
                .withClaim("tenantId", tenantId)
                .withClaim("tenantCode", tenantCode)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withClaim("scope", "tenant")
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(jwtProperties.getExpirationHours(), ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret());

        return JWT.require(algorithm)
                .withIssuer(jwtProperties.getIssuer())
                .build()
                .verify(token);
    }

    public String extractLogin(String token) {
        return validateToken(token).getSubject();
    }

    public Long extractUserId(String token) {
        return validateToken(token).getClaim("userId").asLong();
    }

    public String extractRole(String token) {
        return validateToken(token).getClaim("role").asString();
    }

    public String extractScope(String token) {
        return validateToken(token).getClaim("scope").asString();
    }

    public Long extractTenantId(String token) {
        return validateToken(token).getClaim("tenantId").asLong();
    }

    public String extractTenantCode(String token) {
        return validateToken(token).getClaim("tenantCode").asString();
    }
}
