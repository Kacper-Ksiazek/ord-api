# Document every endpoint with @Operation and @ApiResponses

Each handler method carries an `@Operation(summary, description)` plus an `@ApiResponses` block listing the success code and every expected error (`400`, `401`, `404`, `429`, ...). Error responses use `content = [Content()]` to keep them schema-less, while success responses with a meaningful body may declare an explicit schema. This metadata is the source of the generated TypeScript client, so it must be complete.

## Good

```kotlin
@PostMapping("/otp-verify")
@Operation(
    summary = "Verify OTP code and login",
    description = "Verifies the OTP code and returns a JWT token in the response cookie (AUTH-TOKEN) along with user details."
)
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "200",
            description = "OTP verified successfully, user authenticated",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = UserDTO::class))]
        ),
        ApiResponse(responseCode = "400", description = "Invalid OTP code", content = [Content()]),
        ApiResponse(responseCode = "401", description = "OTP expired or invalid", content = [Content()])
    ]
)
fun verifyOtp(...): Mono<ResponseEntity<UserDTO>> = ...
```

## Bad

```kotlin
@PostMapping("/otp-verify")
// no @Operation summary, no @ApiResponses — error codes undocumented,
// generated TS types lose the response schema
fun verifyOtp(...): Mono<ResponseEntity<UserDTO>> = ...
```
