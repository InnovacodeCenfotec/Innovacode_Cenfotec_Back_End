package com.project.demo.logic.entity.auth;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class PtokenService {

    @Value("${pixlr.api.key}")
    private String pixlrApiKey;

    @Value("${pixlr.api.secret}")
    private String pixlrApiSecret;

    @Value("${pixlr.token.expiration-time}")
    private long pixlrTokenExpiration;

    //pixlr.open-url=http://localhost:4200/image.png
    //pixlr.save-url=http://localhost:4200/api/saveImage

    public String generatePixlrToken(String openUrl, String saveUrl) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", pixlrApiKey);   // API Key
        claims.put("mode", "http");       // Mode
        claims.put("openUrl", openUrl);   // URL to fetch the image: "https://yourdomain.com/image.png"
        claims.put("saveUrl", saveUrl);   // URL to save the edited image "https://yourdomain.com/save-image"

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + pixlrTokenExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(pixlrApiSecret.getBytes());
    }
}


