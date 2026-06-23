# Resolve the authenticated user reactively

The `@AuthenticatedUser` argument resolver returns a `Mono` and is composed into the request pipeline by WebFlux — it must never block to fetch the principal or the user. Derive the principal from `exchange.getPrincipal()` and `flatMap` into the reactive user lookup, applying `switchIfEmpty(Mono.error(...))` when the user is missing.

## Good

```kotlin
override fun resolveArgument(
    parameter: MethodParameter,
    bindingContext: BindingContext,
    exchange: ServerWebExchange
): Mono<Any> {
    return exchange.getPrincipal<Authentication>()
        .flatMap { auth ->
            val email = auth.name
            userRepository
                .findByEmail(email)
                .map { it!!.toDTO() }
                .switchIfEmpty(Mono.error(IllegalArgumentException("User not found")))
        }
        .cast(Any::class.java)
}
```

## Bad

```kotlin
override fun resolveArgument(
    parameter: MethodParameter,
    bindingContext: BindingContext,
    exchange: ServerWebExchange
): Mono<Any> {
    // Blocking inside the resolver stalls the event loop on every request
    val auth = exchange.getPrincipal<Authentication>().block()!!
    val user = userRepository.findByEmail(auth.name).block()!!.toDTO()
    return Mono.just(user)
}
```
