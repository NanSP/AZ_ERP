package com.example.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private static final String SECRET = "trocar-essa-chave-na-fase-3";
    private static final String ISSUER = "az-erp-backend";

    public String generateToken(Long userId, String login, String role, String scope) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(login)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withClaim("scope", scope)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.require(algorithm)
                .withIssuer(ISSUER)
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
}
