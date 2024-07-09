package com.backend.ord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
open class JwtProperties(
    var authCookieName: String = "",
    var userIdClaimName: String = "",
    var secretKey: String = "",
    var expirationTime: Long = 0
)
