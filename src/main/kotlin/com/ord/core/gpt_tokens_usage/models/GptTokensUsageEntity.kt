package com.ord.core.gpt_tokens_usage.models

import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table(name = "gpt_tokens_usage")
data class GptTokensUsageEntity(
    @Id
    override var id: UUID? = null,

    @Column("operation_type")
    val operationType: String,

    val model: String,

    @Column("input_tokens")
    val inputTokens: Int,

    @Column("output_tokens")
    val outputTokens: Int,

    @Column("user_id")
    override var userId: UUID,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource
