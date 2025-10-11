package com.ord.core.auth.services

import reactor.core.publisher.Mono

interface OtpService {
    fun generateAndSaveOtp(email: String): Mono<String>
    fun verifyAndDeleteOtp(email: String, code: String): Mono<String>
}
