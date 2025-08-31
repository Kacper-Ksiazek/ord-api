package com.ord.core.security

import io.jsonwebtoken.Claims
import java.time.Instant

fun Claims.extractSubject(): String = this.subject


fun Claims.extractJti(): String = this.id


fun Claims.isExpired(): Boolean = this.expiration?.toInstant()?.isBefore(Instant.now()) ?: false
