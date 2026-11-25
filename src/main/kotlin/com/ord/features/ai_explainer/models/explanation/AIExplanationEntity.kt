package com.ord.features.ai_explainer.models.explanation

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.shared.models.IdentifiableUserResource
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("ai_explanations")
data class AIExplanationEntity(
    @Id
    override val id: UUID? = null,

    val phrase: String,
    val language: LanguageName,
    val explanation: String,

    val followUps: Json = Json.of("{}"), // JSONB - Map<ExplainPhraseFollowUpAction, String>
    val kind: ExplainPhraseKind? = null,
    val emoji: String? = null,

    override val userId: UUID,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) : IdentifiableUserResource
