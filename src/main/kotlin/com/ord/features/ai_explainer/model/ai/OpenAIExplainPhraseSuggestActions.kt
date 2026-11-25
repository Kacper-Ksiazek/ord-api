package com.ord.features.ai_explainer.model.ai

import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind

/** Raw structured output of the suggest-actions prompt. */
data class OpenAIExplainPhraseSuggestActions(
    val kind: ExplainPhraseKind = ExplainPhraseKind.WORD_OR_PHRASE,
    val suggestedActions: List<ExplainPhraseFollowUpAction> = emptyList(),
    val emoji: String = "",
)
