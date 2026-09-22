package com.ord.core.auth.services

import com.ord.core.auth.model.enums.UiLocale
import reactor.core.publisher.Mono

interface EmailService {
    fun sendOtpEmail(toEmail: String, otpCode: String, locale: UiLocale): Mono<Void>
}
