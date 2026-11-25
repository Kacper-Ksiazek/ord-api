package com.ord.features.ai_explainer.model.enums

import com.ord.core.ai_provider_usage.models.AiProviderUsageOperationType
import com.ord.shared.annotations.ExportToOpenAPI
import com.ord.shared.prompts.AvailablePrompts
import io.swagger.v3.oas.annotations.media.Schema

@ExportToOpenAPI
@Schema(description = "Follow-up action applied to a previous phrase explanation")
enum class ExplainPhraseFollowUpAction(
    val prompt: AvailablePrompts,
    val operationKey: String,
) {
    SIMPLER(
        AvailablePrompts.AI_EXPLAINER_FOLLOW_UP_SIMPLER,
        AiProviderUsageOperationType.AIExplainer.FollowUp.SIMPLER,
    ),
    MORE_EXAMPLES(
        AvailablePrompts.AI_EXPLAINER_FOLLOW_UP_MORE_EXAMPLES,
        AiProviderUsageOperationType.AIExplainer.FollowUp.MORE_EXAMPLES,
    ),
    SIMILAR_EXPRESSIONS(
        AvailablePrompts.AI_EXPLAINER_FOLLOW_UP_SIMILAR_EXPRESSIONS,
        AiProviderUsageOperationType.AIExplainer.FollowUp.SIMILAR_EXPRESSIONS,
    ),
    COMMON_MISTAKES(
        AvailablePrompts.AI_EXPLAINER_FOLLOW_UP_COMMON_MISTAKES,
        AiProviderUsageOperationType.AIExplainer.FollowUp.COMMON_MISTAKES,
    ),
    IN_MY_LANGUAGE(
        AvailablePrompts.AI_EXPLAINER_FOLLOW_UP_IN_MY_LANGUAGE,
        AiProviderUsageOperationType.AIExplainer.FollowUp.IN_MY_LANGUAGE,
    ),
    USED_WITH(
        AvailablePrompts.AI_EXPLAINER_FOLLOW_UP_USED_WITH,
        AiProviderUsageOperationType.AIExplainer.FollowUp.USED_WITH,
    ),
}
