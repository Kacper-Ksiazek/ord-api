package com.ord.core.auth.models

import com.ord.core.user.model.UserEntity
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table(name = "user_sessions")
data class UserSessionEntity(
    var token: String,

    @Column("user_id")
    override var userId: UUID,

    var createdAt: Instant = Instant.now(),

    @Id
    override var id: UUID? = null
) : IdentifiableUserResource {

    @Transient
    override var user: UserEntity? = null
}