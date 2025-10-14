package com.ord.core.auth.services.impl

import com.ord.core.auth.services.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Year

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val resourceLoader: ResourceLoader,
    @Value("\${email.from}") private val fromEmail: String
) : EmailService {

    override fun sendOtpEmail(toEmail: String, otpCode: String): Mono<Void> {
        return Mono.fromCallable {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setFrom(fromEmail)
            helper.setTo(toEmail)
            helper.setSubject("Your OTP Code - ORD")
            helper.setText(buildEmailBody(otpCode), true)

            mailSender.send(message)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .then()
    }

    private fun buildEmailBody(otpCode: String): String {
        val template = resourceLoader
            .getResource("classpath:templates/otp-email.html")
            .inputStream
            .bufferedReader()
            .use { it.readText() }

        return template
            .replace("{{OTP_CODE}}", otpCode)
            .replace("{{YEAR}}", Year.now().value.toString())
    }
}
