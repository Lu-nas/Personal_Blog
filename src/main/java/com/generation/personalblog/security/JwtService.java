package com.generation.personalblog.security;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
	
	public static final String SECRT ="37a29a953661a702b2a4f6e6930df5d3bdbbc6f20c3228fa7b0281a1c9516b1a";

	
	private SecretKey getSignKey() {
	byte[] keyBytes = Decoders.BASE64.decode(SECRT);
		return Keys.hmacShaKeyFor(keyBytes);
	}	
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	// retorna os dados de acesso
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	// checa a data do token
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	// chegam simultanea
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	 // tempo de duração do token 1h aparit do tempo que esta sendo criado
	// junto com a assinatura do token linh68
	private String createToken(Map<String,Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 ))
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}
	
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}
}
