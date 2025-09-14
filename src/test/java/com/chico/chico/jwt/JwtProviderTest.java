package com.chico.chico.jwt;

import com.chico.chico.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtProviderTest {

    private JwtProvider jwtProvider;


    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();

        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", "mysecretkeymysecretkeymysecretkey123");
        ReflectionTestUtils.setField(jwtProvider, "expiration", 3600000L);
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtProvider.generateToken("test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_ShouldContainCorrectEmailAsSubject() {
        String token = jwtProvider.generateToken("test@example.com");
        String email = jwtProvider.extractEmailFromToken(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void generateToken_ShouldHaveExpirationDate() {
        String token = jwtProvider.generateToken("test@example.com");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtProvider.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        assertNotNull(expiration);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtProvider.generateToken("test@example.com");

        boolean isValid = jwtProvider.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() - 1000);

        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(jwtProvider.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtProvider.validateToken(expiredToken);
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        String invalidToken = "this.is.not.a.jwt";

        boolean isValid = jwtProvider.validateToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    void extractEmailFromToken_ShouldReturnCorrectEmail_ForValidToken() {
        String token = jwtProvider.generateToken("test@example.com");

        String email = jwtProvider.extractEmailFromToken(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void extractEmailFromToken_ShouldReturnNull_ForEmptyToken() {
        String token = jwtProvider.extractEmailFromToken("");

        assertNull(token);
    }

    @Test
    void extractEmailFromToken_ShouldReturnErrorMessage_FormInvalidToken() {
        String invalidToken = "this.is.not.a.jwt";

        String result = jwtProvider.extractEmailFromToken(invalidToken);

        assertNotNull(result);
        assertTrue(result.contains("JWT"));
    }
}
