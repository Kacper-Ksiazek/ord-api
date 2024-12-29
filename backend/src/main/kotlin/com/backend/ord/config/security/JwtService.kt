package com.backend.ord.config.security

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.exceptions.REST.ForbiddenException
import com.backend.ord.services.UserService
import com.backend.ord.utils.CookieUtils.getCookieValue
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService(
    private val userService: UserService,
    private val jwtProperties: JwtProperties
) {
    fun getJWTFromRequest(request: HttpServletRequest): String? =
        getCookieValue(jwtProperties.authCookieName, request)

    fun getAuthenticatedUser(request: HttpServletRequest): User? =
        getJWTFromRequest(request)?.let { jwtToken ->
            val userId = extractUserId(jwtToken)
            userService.findById(userId)
        }

    @Throws(ForbiddenException::class)
    fun getAuthenticatedUserOrThrowForbidden(request: HttpServletRequest): User =
        getAuthenticatedUser(request) ?: throw ForbiddenException("User is not authenticated")

    @Throws(ExpiredJwtException::class)
    fun extractUsername(jwtToken: String): String =
        extractClaim(jwtToken) { it.subject }

    fun extractUserId(jwtToken: String): UUID {
        val claimName = jwtProperties.userIdClaimName
        return UUID.fromString(
            extractClaim(jwtToken) { claims -> claims[claimName, String::class.java] }
        )
    }

    fun <T> extractClaim(jwtToken: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(jwtToken)
        return claimsResolver(claims)
    }

    fun extractExpiration(jwtToken: String): Date =
        extractClaim(jwtToken) { it.expiration }

    fun generateToken(userDetails: UserDetails): String =
        generateToken(emptyMap(), userDetails)

    fun generateToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails
    ): String {
        val currentTime = System.currentTimeMillis()
        val expirationTime = jwtProperties.expirationTime

        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(currentTime))
            .setExpiration(Date(currentTime + expirationTime))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }

    private val signingKey: Key
        get() {
            val keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    private fun extractAllClaims(jwtToken: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(jwtToken)
            .body
}
