package com.ord.features.bank_group.model

import com.ord.core.user.model.UserEntity
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("bank_groups")
data class BankGroupEntity(
    @Id
    override var id: UUID = UUID.randomUUID(),

    var name: String,
    var color: String,

    override val userId: UUID,

    var createdAt: Instant = Instant.now(),

    @Transient override var user: UserEntity,
) : IdentifiableUserResource