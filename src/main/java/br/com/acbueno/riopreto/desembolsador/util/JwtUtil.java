package br.com.acbueno.riopreto.desembolsador.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	 private final Key key;

	    public JwtUtil(@Value("${jwt.secret}") String secret) {
	        this.key = Keys.hmacShaKeyFor(secret.getBytes());
	    }

	    public String validateAndGetSubject(String token) {
	        return Jwts.parserBuilder().setSigningKey(key).build()
	                .parseClaimsJws(token)
	                .getBody()
	                .getSubject();
	    }
	    
	    public String generateToken(String subject) {
	        return Jwts.builder()
	                .setSubject(subject)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1h
	                .signWith(key, SignatureAlgorithm.HS256)
	                .compact();
	    }

}
