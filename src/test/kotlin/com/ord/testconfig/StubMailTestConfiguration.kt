package com.ord.testconfig

import jakarta.mail.internet.MimeMessage
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessagePreparator
import java.io.InputStream

@TestConfiguration
open class StubMailTestConfiguration {

    @Bean
    @Primary
    fun javaMailSender(): JavaMailSender = object : JavaMailSender {
        private val delegate = JavaMailSenderImpl()

        override fun createMimeMessage(): MimeMessage = delegate.createMimeMessage()

        override fun createMimeMessage(contentStream: InputStream): MimeMessage =
            delegate.createMimeMessage(contentStream)

        override fun send(mimeMessage: MimeMessage) = Unit

        override fun send(vararg mimeMessages: MimeMessage) = Unit

        override fun send(mimeMessagePreparator: MimeMessagePreparator) = Unit

        override fun send(vararg mimeMessagePreparators: MimeMessagePreparator) = Unit

        override fun send(simpleMessage: SimpleMailMessage) = Unit

        override fun send(vararg simpleMessages: SimpleMailMessage) = Unit
    }
}
