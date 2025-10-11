package com.ord.core.auth.services.impl

import com.ord.core.auth.services.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    @Value("\${otp.email.from}") private val fromEmail: String
) : EmailService {

    override fun sendOtpEmail(toEmail: String, otpCode: String): Mono<Void> {
        return Mono.fromCallable {
            val message = SimpleMailMessage()
            message.setFrom(fromEmail)
            message.setTo(toEmail)
            message.subject = "Your OTP Code"
            message.text = buildEmailBody(otpCode)

            mailSender.send(message)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .then()
    }

    private fun buildEmailBody(otpCode: String): String {
        return """
            Hello,

            Your OTP code is: $otpCode

            This code will expire in 10 minutes.

            If you did not request this code, please ignore this email.

            Best regards,
            ORD API Team
        """.trimIndent()
    }
}
