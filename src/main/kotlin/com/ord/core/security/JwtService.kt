package com.ord.core.security

import com.ord.config.properties.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class JwtService(private val jwtProperties: JwtProperties) {
    private val key = Keys.hmacShaKeyFor(
        jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8)
    )


    fun createToken(
        subject: String,
        jti: String = UUID.randomUUID().toString(),
        issuedAt: Instant = Instant.now(),
    ): String {
        val expiration = issuedAt.plusSeconds(jwtProperties.expirationTime)

        return Jwts.builder()
            .subject(subject)
            .id(jti)
            .issuer(jwtProperties.issuer)
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiration))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }


    fun parseAndValidate(token: String): Jws<Claims> {
        return Jwts.parser()
            .verifyWith(key)
            .requireIssuer(jwtProperties.issuer)
            .build()
            .parseSignedClaims(token)
    }


    fun parseAllowExpired(token: String): Claims =
        try {
            parseAndValidate(token).payload
        } catch (ex: ExpiredJwtException) {
            ex.claims
        }
}
