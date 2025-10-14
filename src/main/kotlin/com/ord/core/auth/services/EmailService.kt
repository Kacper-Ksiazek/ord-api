package com.ord.core.auth.services

import reactor.core.publisher.Mono

interface EmailService {
    fun sendOtpEmail(toEmail: String, otpCode: String): Mono<Void>
}
