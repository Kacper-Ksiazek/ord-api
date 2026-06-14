package com.ord.core.auth.services.impl

import com.ord.config.properties.OtpProperties
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.auth.services.OtpService
import com.ord.exceptions.REST.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.random.Random

@Service
class OtpServiceImpl(
    private val otpCodeRepository: OtpCodeRepository,
    private val encoder: PasswordEncoder,
    private val otpProperties: OtpProperties,
    private val env: Environment
) : OtpService {

    private val logger = LoggerFactory.getLogger(OtpServiceImpl::class.java)
    private val isTestingEnv: Boolean = env.activeProfiles.contains("test")
    private val isLocalEnv: Boolean = env.activeProfiles.contains("local")

    override fun generateAndSaveOtp(email: String): Mono<String> {
        val otpCode = generateOtpCode(email)
        val hashedCode = encoder.encode(otpCode)
        val expiresAt = Instant.now().plusSeconds(otpProperties.expirationMinutes * 60)

        return otpCodeRepository
            .deleteByUserEmail(email)
            .then(
                otpCodeRepository.save(
                    OtpCodeEntity(
                        code = hashedCode,
                        expiresAt = expiresAt,
                        userEmail = email
                    )
                )
            )
            .thenReturn(otpCode)
    }

    override fun verifyAndDeleteOtp(email: String, code: String): Mono<String> {
        return otpCodeRepository
            .findByUserEmail(email)
            .flatMap { otpEntity ->
                when {
                    Instant.now().isAfter(otpEntity.expiresAt) -> {
                        otpCodeRepository
                            .delete(otpEntity)
                            .then(Mono.error(UnauthorizedException("OTP code has expired")))
                    }

                    !encoder.matches(code, otpEntity.code) -> {
                        Mono.error(UnauthorizedException("Invalid OTP code"))
                    }

                    else -> {
                        otpCodeRepository
                            .delete(otpEntity)
                            .thenReturn(otpEntity.userEmail)
                    }
                }
            }
            .switchIfEmpty(
                Mono.error(UnauthorizedException("No OTP code found for this email"))
            )
    }

    private fun generateOtpCode(email: String): String {
        return when {
            isTestingEnv -> "000000"

            isLocalEnv -> {
                val code = otpProperties.codeForWhitelisted.ifBlank { "123456" }
                logger.info("[LOCAL] OTP for {}: {} (SMTP skipped in local profile)", email, code)
                code
            }

            otpProperties.isEmailWhitelisted(email) && otpProperties.codeForWhitelisted.isNotBlank() -> {
                otpProperties.codeForWhitelisted
            }

            else -> Random
                .nextInt(0, 1000000)
                .toString()
                .padStart(6, '0')
        }
    }
}
