# Never log or return secrets and OTP codes

Raw OTP codes, JWTs, signing keys, and credentials must never be logged or echoed in API responses. The OTP code is generated, used to build the email body, and otherwise only persisted as a BCrypt hash — it never appears in a log line or a response payload. `otp-verify` returns a `UserDTO`, not the token (the token goes in the cookie).

## Good

```kotlin
override fun sendOtpEmail(toEmail: String, otpCode: String): Mono<Void> {
    return Mono.fromCallable {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")
        helper.setTo(toEmail)
        helper.setSubject("Your OTP Code - ORD")
        helper.setText(buildEmailBody(otpCode), true) // delivered only to the user's inbox
        mailSender.send(message)
    }.subscribeOn(Schedulers.boundedElastic()).then()
}
```

## Bad

```kotlin
override fun requestOtp(email: String): Mono<Void> {
    return otpService.generateAndSaveOtp(email)
        .flatMap { otpCode ->
            // Leaks the plaintext OTP into application logs
            log.info("Generated OTP $otpCode for $email")
            emailService.sendOtpEmail(email, otpCode)
        }
}
```
