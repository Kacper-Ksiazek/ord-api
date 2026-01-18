package com.ord.features.conversation.api.facades.helpers.ai_responses

import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedGrammarTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedPhraseTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedVocabularyTip

/**
 * Domain model for AI message learning tips.
 * Contains categorized learning annotations with Set for preventing duplicates.
 */
data class AIMessageLearningTips(
    val grammarTips: Set<AnnotatedGrammarTip>,
    val vocabularyTips: Set<AnnotatedVocabularyTip>,
    val phraseTips: Set<AnnotatedPhraseTip>
)
