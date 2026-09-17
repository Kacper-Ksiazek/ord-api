package com.ord.core.security

import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.HexFormat

@Service
class SessionTokenService {
    fun generateRawToken(): String {
        val bytes = ByteArray(TOKEN_BYTE_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun hash(rawToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(rawToken.toByteArray(StandardCharsets.UTF_8))
        return HexFormat.of().formatHex(digest)
    }

    companion object {
        private const val TOKEN_BYTE_LENGTH = 32
        private val secureRandom = SecureRandom()
    }
}
