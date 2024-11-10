package com.project.demo.logic.entity.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;

@Service
public class OAuth2AuthenticationService {
    private final JwtDecoder jwtDecoder;

    public OAuth2AuthenticationService() {
        this.jwtDecoder = JwtDecoders.fromOidcIssuerLocation("https://accounts.google.com");
    }

    public OAuth2User verifyGoogleToken(String idToken) {
        try {
            // Decodificar el token JWT
            Jwt jwt = jwtDecoder.decode(idToken);

            // Validar el token usando un validador de timestamp
            OAuth2TokenValidator<Jwt> validator = new JwtTimestampValidator();
            OAuth2TokenValidatorResult validation = validator.validate(jwt);

            if (validation.hasErrors()) {
                return null; // Token inválido
            }

            // Si la validación es exitosa, se devuelve el usuario de Google
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("USER")),
                    jwt.getClaims(),
                    "email"
            );
        } catch (JwtException e) {
            e.printStackTrace();
            return null; // Error en la verificación
        }
    }
}
