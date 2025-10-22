package com.ord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "otp")
class OtpProperties(
    var expirationMinutes: Long = 10,
    var whitelistedEmails: String = "",
    var codeForWhitelisted: String = ""
) {
    fun getWhitelistedEmailsList(): List<String> {
        return if (whitelistedEmails.isBlank()) {
            emptyList()
        } else {
            whitelistedEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }

    fun isEmailWhitelisted(email: String): Boolean {
        return getWhitelistedEmailsList().contains(email.trim())
    }
}