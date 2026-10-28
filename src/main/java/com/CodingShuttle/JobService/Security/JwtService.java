package com.CodingShuttle.JobService.Security;

import com.CodingShuttle.JobService.Exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class JwtService {
    private static final int MAX_TOKEN_LENGTH = 4096;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @PostConstruct
    void verifySecretKeyConfigured(){
        if(jwtSecretKey == null || jwtSecretKey.isBlank()){
            throw new IllegalStateException("The 'jwt.secretKey' property is missing. It must match the secret used by user-service.");
        }
    }

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Long getUserIdFromToken(String token){
        if(token == null || token.isBlank()){
            throw new UnauthorizedException("Access token is missing.");
        }

        Claims claims;
        try{
            claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex){
            throw new UnauthorizedException("Access token has expired");
        } catch (JwtException | IllegalArgumentException ex){
            log.debug("Rejected access token: {}", ex.getMessage());
            throw new UnauthorizedException("Access token is invalid.");
        }

        String subject = claims.getSubject();
        if(subject == null || subject.isBlank()){
            throw new UnauthorizedException("Access token is invalid: subject claim is missing.");
        }
        try{
            long userId = Long.parseLong(subject.trim());
            if(userId <= 0){
                throw new UnauthorizedException("Access token is invalid: subject claim must be a positive integer.");
            }
            return userId;
        }catch (NumberFormatException ex){
            throw new UnauthorizedException("Access token is invalid: subject claim must be a positive integer.");
        }
    }
}
