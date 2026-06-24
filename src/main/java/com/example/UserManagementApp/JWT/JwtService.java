package com.example.UserManagementApp.JWT;

import com.example.UserManagementApp.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


@Service

public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtexpiration;

    //Extract the user Id
    public Long extractUserId(String jwtToken){
        String userIdStr = extractClaim(jwtToken, claims -> claims.get("userId", String.class));

        return userIdStr != null ? Long.parseLong(userIdStr) : null;
    }

    //Extract the username
    public String extractUsername(String jwtToken){
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T  extractClaim(String jwtToken, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public SecretKey getSignInKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user){
        return generateToken(new HashMap(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user){
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("userId", user.getId().toString());

        return Jwts
                .builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtexpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public Boolean isTokenValid(String jwtToken, User user){

        final Long UserIdFromToken = extractUserId(jwtToken);

        final Long userIdDetails = user.getId();

        return(UserIdFromToken != null && UserIdFromToken.equals(userIdDetails)
                        && isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken){
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken){
        return extractClaim(jwtToken, Claims::getExpiration);
    }
}
