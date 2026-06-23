# Store OTP codes hashed with an expiry

OTP codes are sensitive credentials: persist only the BCrypt hash (`encoder.encode`), set an `expiresAt` from `OtpProperties.expirationMinutes`, and verify with `encoder.matches`. Reject and delete expired codes, and never store or compare the code in plaintext. Each new request replaces the previous code for that email.

## Good

```kotlin
override fun generateAndSaveOtp(email: String): Mono<String> {
    val otpCode = generateOtpCode(email)
    val hashedCode = encoder.encode(otpCode)
    val expiresAt = Instant.now().plusSeconds(otpProperties.expirationMinutes * 60)

    return otpCodeRepository
        .deleteByUserEmail(email)
        .then(
            otpCodeRepository.save(
                OtpCodeEntity(code = hashedCode, expiresAt = expiresAt, userEmail = email)
            )
        )
        .thenReturn(otpCode)
}
```

## Bad

```kotlin
override fun verifyAndDeleteOtp(email: String, code: String): Mono<String> {
    return otpCodeRepository.findByUserEmail(email)
        .flatMap { otpEntity ->
            // Plaintext storage + equality check, and no expiry enforcement
            if (otpEntity.code == code) Mono.just(otpEntity.userEmail)
            else Mono.error(UnauthorizedException("Invalid OTP code"))
        }
}
```
