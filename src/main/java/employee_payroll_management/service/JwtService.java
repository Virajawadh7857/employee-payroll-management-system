package employee_payroll_management.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

@Service
public class JwtService {

	private final SecretKey signingKey;
	private final long expiration;

	public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {

		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));

		this.expiration = expiration;
	}

	public String generateToken(String username) {

		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiration);

		return Jwts.builder().subject(username).issuedAt(now).expiration(expiry).signWith(signingKey).compact();
	}

	public String extractUsername(String token) {

		return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean isTokenValid(String token, String username) {

		try {
			String tokenUsername = extractUsername(token);

			return tokenUsername.equals(username);
		} catch (Exception ex) {
			return false;
		}
	}

}