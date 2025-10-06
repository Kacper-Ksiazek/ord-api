package com.ord.features.bank.model

import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("banks")
data class BankEntity(
    @Id
    override val id: UUID? = null,

    val name: String,
    val description: String,

    override val userId: UUID,
    var groupId: UUID? = null,

    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource
