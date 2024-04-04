package com.backend.ord.config.security;

import com.backend.ord.config.properties.JwtProperties;
import com.backend.ord.domain.entities.User;
import com.backend.ord.services.UserService;
import com.backend.ord.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserService userService;
    private final JwtProperties jwtProperties;

    public Optional<String> getJWTFromRequest(HttpServletRequest request) {
        return CookieUtils.getCookieValue(
                jwtProperties.getAuthCookieName(),
                request
        );
    }

    public Optional<User> getAuthenticatedUser(HttpServletRequest request) {
        Optional<String> jwtToken = getJWTFromRequest(request);

        if (jwtToken.isEmpty()) return Optional.empty();

        return userService.findUserByAuthToken(jwtToken.get());
    }

    public String extractUsername(String jwtToken) throws ExpiredJwtException {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public UUID extractUserId(String jwtToken) {
        String claimName = jwtProperties.getUserIdClaimName();

        return UUID.fromString(
                extractClaim(jwtToken, claims -> claims.get(claimName, String.class))
        );
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        // Current time in milliseconds
        final long currentTime = System.currentTimeMillis();

        long JWT_EXPIRATION_TIME = jwtProperties.getExpirationTime();

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + JWT_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // ----------------
    // ### Private helper methods

    private Key getSigningKey() {
        String JWT_SECRET = jwtProperties.getSecretKey();

        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
