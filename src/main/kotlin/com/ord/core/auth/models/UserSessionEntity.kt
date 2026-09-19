package com.ord.core.auth.models

import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table(name = "user_sessions")
data class UserSessionEntity(
    @Column("token_hash")
    var tokenHash: String,

    @Column("user_id")
    override var userId: UUID,

    var createdAt: Instant = Instant.now(),

    @Column("last_seen_at")
    var lastSeenAt: Instant = Instant.now(),

    @Column("idle_expires_at")
    var idleExpiresAt: Instant = Instant.now(),

    @Column("absolute_expires_at")
    var absoluteExpiresAt: Instant = Instant.now(),

    @Id
    override var id: UUID? = null
) : IdentifiableUserResource {
    fun isExpired(now: Instant = Instant.now()): Boolean {
        return !now.isBefore(idleExpiresAt) || !now.isBefore(absoluteExpiresAt)
    }
}
