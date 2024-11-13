package com.project.demo.logic.entity.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OAuth2AuthenticationService {
    private final JwtService jwtService;

    public OAuth2AuthenticationService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public OAuth2User verifyToken(String token) {
        try {
            // Extraer las claims del token
            Claims claims = jwtService.extractAllClaims(token);

            // Verificar si el token ha expirado
            if (jwtService.isTokenExpired(token)) {
                return null; // El token ha expirado
            }

            // Extraer el email o username (asumiendo que viene en las claims)
            String email = claims.get("email", String.class);
            if (email == null) {
                return null; // Token inválido si no contiene email
            }

            // Crear y retornar un OAuth2User
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("USER")),
                    claims,
                    "email"
            );
        } catch (JwtException e) {
            e.printStackTrace();
            return null; // Error en la verificación
        }
    }
}
