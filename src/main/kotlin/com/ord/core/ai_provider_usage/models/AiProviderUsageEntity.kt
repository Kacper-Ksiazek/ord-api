package com.ord.core.ai_provider_usage.models

import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Table(name = "ai_provider_usage")
data class AiProviderUsageEntity(
    @Id
    override var id: UUID? = null,

    @Column("provider")
    val provider: AiProvider,

    @Column("operation_type")
    val operationType: String,

    val model: String,

    @Column("voice_id")
    val voiceId: String? = null,

    @Column("input_units")
    val inputUnits: Int,

    @Column("output_units")
    val outputUnits: Int,

    @Column("unit_type")
    val unitType: AiUsageUnitType,

    @Column("estimated_price")
    val estimatedPrice: BigDecimal,

    @Column("user_id")
    override var userId: UUID,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource
