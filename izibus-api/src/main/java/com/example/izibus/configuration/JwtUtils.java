package com.example.izibus.configuration;

import com.example.izibus.entities.Administrateur;
import com.example.izibus.entities.Compagnie;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expirationTime}")
    private Long expirationTime;

    // Générer un token à partir de l'email
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }


    // génère le token de la compagnie de bus qui se connecte comportant ainsi l'id de la compagnie pour permette de faciliter les actions dans le frontend angular
    public String generateTokenCompagnie(Compagnie compagnie) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("compagnieId", compagnie.getId()); // CLAIM ESSENTIEL
        claims.put("role", "COMPAGNIE");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(compagnie.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // génère le token de l'Admin qui se connecte comportant l'id de l'admin pour permettre de faciler les actions de l'adminn dans le frontend
    public String generateTokenAdmin(Administrateur admin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId()); // CLAIM pour l'ID admin
        claims.put("role", "ADMIN");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(admin.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    // Créer un token avec les informations
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Obtenir la clé de signature
    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Extraire l'email du token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T>  T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractCompagnieId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("compagnieId", Long.class);
    }

    // Valider le token et vérifier que l'email dans le token correspond à l'email de l'utilisateur
    public Boolean validateToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Comparer l'email dans le token avec celui de l'utilisateur
    }

    private Date extractExpirationDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());

    }
}
