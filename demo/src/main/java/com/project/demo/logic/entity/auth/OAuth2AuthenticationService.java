package com.project.demo.logic.entity.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

@Service
public class OAuth2AuthenticationService {
    private final JwtService jwtService;

    public OAuth2AuthenticationService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public OAuth2User verifyGoogleToken(String idToken) {
        try {
            Claims claims = jwtService.extractAllClaims(idToken);

            String issuer = claims.getIssuer();
            if (!"https://accounts.google.com".equals(issuer)) {
                return null;
            }

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("USER")),
                    claims,
                    "email"
            );
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }
}
