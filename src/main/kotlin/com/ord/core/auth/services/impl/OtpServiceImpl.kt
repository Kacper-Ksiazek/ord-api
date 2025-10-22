package com.ord.core.auth.services.impl

import com.ord.config.properties.OtpProperties
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.auth.services.OtpService
import com.ord.exceptions.REST.UnauthorizedException
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

    private val isTestingEnv: Boolean = env.activeProfiles.contains("test")

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
            // For test environment, always return a fixed code for predictability
            isTestingEnv -> "000000"

            // For whitelisted emails, use the configured code to skip email sending during development
            otpProperties.isEmailWhitelisted(email) && otpProperties.codeForWhitelisted.isNotBlank() -> {
                otpProperties.codeForWhitelisted
            }

            // For all other cases, generate a random 6-digit code (including codes with leading zeros)
            else -> Random
                .nextInt(0, 1000000)
                .toString()
                .padStart(6, '0')
        }
    }
}
