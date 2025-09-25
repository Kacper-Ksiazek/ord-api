package com.ord.features.bank.model

import com.ord.core.user.model.UserEntity
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("banks")
data class BankEntity(
    @Id
    override var id: UUID = UUID.randomUUID(),

    val name: String,
    val description: String,

    override val userId: UUID,
    var bankGroupId: UUID? = null,

    var createdAt: Instant = Instant.now(),

    @Transient var bankGroup: BankGroupEntity? = null
) : IdentifiableUserResource
