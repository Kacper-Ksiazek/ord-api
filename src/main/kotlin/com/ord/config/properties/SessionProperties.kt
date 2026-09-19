package com.ord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "session")
class SessionProperties(
    var cookieName: String = "AUTH-TOKEN",
    var cookieSecure: Boolean = false,
    var cookieSameSite: String = "Lax",
    var idleTimeout: Duration = Duration.ofDays(7),
    var absoluteTimeout: Duration = Duration.ofDays(30),
    var slideInterval: Duration = Duration.ofSeconds(60),
)
