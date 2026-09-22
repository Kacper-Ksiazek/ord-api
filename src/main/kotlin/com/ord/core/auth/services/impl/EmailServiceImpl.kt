package com.ord.core.auth.services.impl

import com.ord.core.auth.email.OtpEmailCopy
import com.ord.core.auth.email.OtpEmailCopyCatalog
import com.ord.core.auth.model.enums.UiLocale
import com.ord.core.auth.services.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Year

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val resourceLoader: ResourceLoader,
    @Value("\${email.from}") private val fromEmail: String,
    @Value("\${email.app-public-url:http://localhost:5173}") private val appPublicUrl: String,
) : EmailService {

    override fun sendOtpEmail(toEmail: String, otpCode: String, locale: UiLocale): Mono<Void> {
        return Mono.fromCallable {
            val copy = OtpEmailCopyCatalog.forLocale(locale)
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setFrom(fromEmail)
            helper.setTo(toEmail)
            helper.setSubject(copy.subject)
            helper.addInline(
                LOGO_CONTENT_ID,
                resourceLoader.getResource("classpath:templates/ord-logo-email.png"),
            )
            helper.setText(buildEmailBody(toEmail, otpCode, copy), true)

            mailSender.send(message)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .then()
    }

    private fun buildEmailBody(toEmail: String, otpCode: String, copy: OtpEmailCopy): String {
        val template = resourceLoader
            .getResource("classpath:templates/otp-email.html")
            .inputStream
            .bufferedReader()
            .use { it.readText() }

        val year = Year.now().value.toString()
        val preheader = copy.preheader.replace("{{OTP_CODE}}", otpCode)
        val footerTagline = copy.footerTagline.replace("{{YEAR}}", year)

        return template
            .replace("{{HTML_LANG}}", copy.htmlLang)
            .replace("{{PAGE_TITLE}}", copy.subject)
            .replace("{{PREHEADER}}", preheader)
            .replace("{{HEADING}}", copy.heading)
            .replace("{{INTRO}}", copy.intro)
            .replace("{{BUTTON_LABEL}}", copy.buttonLabel)
            .replace("{{EXPIRY_NOTE}}", copy.expiryNote)
            .replace("{{IGNORE_NOTE}}", copy.ignoreNote)
            .replace("{{FOOTER_TAGLINE}}", footerTagline)
            .replace("{{OTP_CODE}}", otpCode)
            .replace("{{OTP_CELLS}}", buildOtpCells(otpCode))
            .replace("{{LOGIN_URL}}", buildLoginUrl(toEmail, otpCode))
            .replace("{{YEAR}}", year)
    }

    private fun buildLoginUrl(email: String, otpCode: String): String {
        val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8)
        val encodedCode = URLEncoder.encode(otpCode, StandardCharsets.UTF_8)
        return "${appPublicUrl.trimEnd('/')}/login?email=$encodedEmail&code=$encodedCode"
    }

    private fun buildOtpCells(otpCode: String): String {
        val cellStyle =
            "width:48px;height:56px;border:1px solid #e7e4dc;border-radius:10px;" +
                "background-color:#ffffff;font-size:24px;font-weight:500;color:#1c1b18;" +
                "text-align:center;vertical-align:middle;"

        return otpCode
            .take(6)
            .padEnd(6, ' ')
            .map { char ->
                val content = if (char == ' ') "&#160;" else char
                """<td align="center" style="$cellStyle">$content</td>"""
            }
            .joinToString(separator = "")
    }

    companion object {
        private const val LOGO_CONTENT_ID = "ordLogo"
    }
}
