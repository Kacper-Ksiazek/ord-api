package com.ord.core.auth.api

import com.ord.core.auth.email.OtpCopyPageRenderer
import com.ord.exceptions.REST.BadRequestException
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
class OtpCopyPageController(
    @Value("\${email.login-url:http://localhost:5173/login}")
    private val loginUrl: String,
) {
    @GetMapping("/public/otp-copy", produces = [MediaType.TEXT_HTML_VALUE])
    fun copyPage(@RequestParam code: String): String {
        if (!OTP_CODE_PATTERN.matches(code)) {
            throw BadRequestException("Invalid OTP code")
        }

        return OtpCopyPageRenderer.render(code = code, loginUrl = loginUrl)
    }

    companion object {
        private val OTP_CODE_PATTERN = Regex("^[0-9]{6}$")
    }
}
