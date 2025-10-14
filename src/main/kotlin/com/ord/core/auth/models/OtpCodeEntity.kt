package com.ord.core.auth.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table(name = "otp_codes")
data class OtpCodeEntity(
    @Id
    val id: UUID? = null,

    @Column("code")
    val code: String,

    @Column("expires_at")
    val expiresAt: Instant,

    @Column("user_email")
    val userEmail: String,

    @Column("created_at")
    val createdAt: Instant = Instant.now()
)
