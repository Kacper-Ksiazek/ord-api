package com.ord.core.security

import io.jsonwebtoken.Claims

fun Claims.extractSubject(): String = this.subject
