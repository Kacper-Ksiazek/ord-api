package com.ord.features.ai_explainer

import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.shared.prompts.structured_outputs.features.ai_explainer.aiExplainerSuggestActionsSchema
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("- ExplainPhraseKind")
class ExplainPhraseKindTest {
    @Test
    fun `grammar questions never offer word-only actions`() {
        val actions = ExplainPhraseKind.GRAMMAR_OR_CONCEPT.rankActions(ExplainPhraseFollowUpAction.entries)

        actions shouldNotContain ExplainPhraseFollowUpAction.SIMILAR_EXPRESSIONS
        actions shouldNotContain ExplainPhraseFollowUpAction.USED_WITH
    }

    @Test
    fun `model ranking goes first, unranked allowed actions follow in default order`() {
        val actions = ExplainPhraseKind.WORD_OR_PHRASE.rankActions(
            listOf(ExplainPhraseFollowUpAction.USED_WITH, ExplainPhraseFollowUpAction.USED_WITH)
        )

        actions.first() shouldBe ExplainPhraseFollowUpAction.USED_WITH
        actions.size shouldBe ExplainPhraseKind.WORD_OR_PHRASE.allowedActions.size
        actions.distinct() shouldBe actions
    }

    @Test
    fun `suggest-actions schema enums stay in sync with the Kotlin enums`() {
        @Suppress("UNCHECKED_CAST")
        val properties = aiExplainerSuggestActionsSchema.schema["properties"] as Map<String, Map<String, Any>>

        (properties.getValue("kind")["enum"] as List<*>).toSet() shouldBe ExplainPhraseKind.entries.map { it.name }.toSet()
        val actionEnum = (properties.getValue("suggestedActions")["items"] as Map<*, *>)["enum"] as List<*>
        actionEnum.toSet() shouldBe ExplainPhraseFollowUpAction.entries.map { it.name }.toSet()
    }
}
