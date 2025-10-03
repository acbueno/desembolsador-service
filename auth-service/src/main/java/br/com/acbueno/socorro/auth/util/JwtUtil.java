package br.com.acbueno.socorro.auth.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



@Component
public class JwtUtil {
	
	private final Key key;
	
	private final Long expiration;

	public JwtUtil(@Value("${jwt.secret}") Key key, @Value("${jwt.expiration}")  Long expiration) {
		this.key = key;
		this.expiration = expiration;
	}
	
	public String generateToken(String subject) {
		return Jwts.builder()
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public String validateToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
	}

}
