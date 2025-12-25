package com.ord.features.conversation.models.conversation_ai_message_learning_tips

import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedCulturalTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedGrammarTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedIdiomTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedVocabularyTip
import java.util.UUID

data class ConversationAIMessageLearningTipsDTO(
    val id: UUID,

    val grammarTips: Set<AnnotatedGrammarTip>,
    val vocabularyTips: Set<AnnotatedVocabularyTip>,
    val idiomTips: Set<AnnotatedIdiomTip>,
    val culturalTips: Set<AnnotatedCulturalTip>,

    val messageId: UUID,
)
