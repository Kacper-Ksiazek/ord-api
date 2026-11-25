package com.ord.features.ai_explainer.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.features.ai_explainer.repositories.AIExplanationRepositoryCustomMethods
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class AIExplanationRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate
) : AIExplanationRepositoryCustomMethods {

    override fun insertIfAbsent(
        id: UUID,
        userId: UUID,
        phrase: String,
        language: LanguageName,
        explanation: String
    ): Mono<Long> {
        val query = """
            INSERT INTO ai_explanations (id, user_id, phrase, language, explanation)
            VALUES (:id, :userId, :phrase, CAST(:language AS language_name), :explanation)
            ON CONFLICT (id) DO NOTHING
        """

        return template.databaseClient
            .sql(query)
            .bind("id", id)
            .bind("userId", userId)
            .bind("phrase", phrase)
            .bind("language", language.name)
            .bind("explanation", explanation)
            .fetch()
            .rowsUpdated()
    }

    override fun updateFollowUpText(
        id: UUID,
        userId: UUID,
        action: ExplainPhraseFollowUpAction,
        text: String
    ): Mono<Long> {
        val query = """
            UPDATE ai_explanations
            SET follow_ups = follow_ups || jsonb_build_object(CAST(:action AS text), CAST(:text AS text)),
                updated_at = CURRENT_TIMESTAMP
            WHERE id = :id AND user_id = :userId
        """

        return template.databaseClient
            .sql(query)
            .bind("action", action.name)
            .bind("text", text)
            .bind("id", id)
            .bind("userId", userId)
            .fetch()
            .rowsUpdated()
    }

    override fun updateClassification(
        id: UUID,
        userId: UUID,
        kind: ExplainPhraseKind,
        emoji: String?
    ): Mono<Long> {
        val query = """
            UPDATE ai_explanations
            SET kind = :kind, emoji = CAST(:emoji AS varchar), updated_at = CURRENT_TIMESTAMP
            WHERE id = :id AND user_id = :userId
        """

        val spec = template.databaseClient
            .sql(query)
            .bind("kind", kind.name)
            .bind("id", id)
            .bind("userId", userId)

        return (if (emoji != null) spec.bind("emoji", emoji) else spec.bindNull("emoji", String::class.java))
            .fetch()
            .rowsUpdated()
    }
}
