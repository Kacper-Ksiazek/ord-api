# Emit errors via Mono.error inside the reactive chain

In reactive code, surface errors with `Mono.error(...)` / `Flux.error(...)` returned from operators (`flatMap`, `switchIfEmpty`, etc.) instead of using `throw` inside those operators. Throwing inside a lambda that is expected to return a publisher breaks the reactive contract and can bypass the centralized error handling. `throw` is acceptable only in plain (non-reactive) helper functions.

## Good

```kotlin
import com.ord.exceptions.REST.UnauthorizedException

// OtpServiceImpl
when {
    otpEntity.isExpired() ->
        otpCodeRepository
            .delete(otpEntity)
            .then(Mono.error(UnauthorizedException("OTP code has expired")))

    !encoder.matches(code, otpEntity.code) ->
        Mono.error(UnauthorizedException("Invalid OTP code"))
}
```

## Bad

```kotlin
when {
    otpEntity.isExpired() ->
        otpCodeRepository
            .delete(otpEntity)
            // throwing inside the flatMap lambda instead of emitting an error signal
            .map { throw UnauthorizedException("OTP code has expired") }

    !encoder.matches(code, otpEntity.code) ->
        throw UnauthorizedException("Invalid OTP code")
}
```
