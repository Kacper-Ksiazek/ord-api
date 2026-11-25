package com.ord.features.ai_explainer.model.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@ExportToOpenAPI
@Schema(description = "What the learner asked the explainer about; decides which refine actions make sense")
enum class ExplainPhraseKind(
    val allowedActions: List<ExplainPhraseFollowUpAction>,
) {
    WORD_OR_PHRASE(
        listOf(
            ExplainPhraseFollowUpAction.MORE_EXAMPLES,
            ExplainPhraseFollowUpAction.SIMPLER,
            ExplainPhraseFollowUpAction.SIMILAR_EXPRESSIONS,
            ExplainPhraseFollowUpAction.COMMON_MISTAKES,
            ExplainPhraseFollowUpAction.USED_WITH,
            ExplainPhraseFollowUpAction.IN_MY_LANGUAGE,
        ),
    ),
    GRAMMAR_OR_CONCEPT(
        listOf(
            ExplainPhraseFollowUpAction.MORE_EXAMPLES,
            ExplainPhraseFollowUpAction.SIMPLER,
            ExplainPhraseFollowUpAction.COMMON_MISTAKES,
            ExplainPhraseFollowUpAction.IN_MY_LANGUAGE,
        ),
    );

    /**
     * All actions that fit this kind, ordered by the model's ranking first. The model can only reorder
     * [allowedActions]: anything outside them is dropped and unranked ones keep their default order.
     */
    fun rankActions(suggested: List<ExplainPhraseFollowUpAction>): List<ExplainPhraseFollowUpAction> {
        val ranked = suggested.filter { it in allowedActions }.distinct()

        return ranked + allowedActions.filterNot { it in ranked }
    }
}
