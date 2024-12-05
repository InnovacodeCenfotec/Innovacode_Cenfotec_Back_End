package com.project.demo.logic.entity.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class GoogleTokenService {

    // Reemplaza esto con tu Client ID de Google
    private static final String CLIENT_ID = "586708376606-g1577kothlf55g5e8ijle3tcl2vvjnnu.apps.googleusercontent.com";

    public OAuth2User verifyToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                Map<String, Object> attributes = Map.of(
                        "sub", payload.getSubject(),
                        "email", payload.getEmail(),
                        "name", payload.get("name"),
                        "picture", payload.get("picture")
                );

                return new DefaultOAuth2User(
                        Collections.singleton(new SimpleGrantedAuthority("USER")),
                        attributes,
                        "sub"
                );
            } else {
                throw new IllegalArgumentException("Token inválido o expirado");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar el token: " + e.getMessage(), e);
        }
    }
}