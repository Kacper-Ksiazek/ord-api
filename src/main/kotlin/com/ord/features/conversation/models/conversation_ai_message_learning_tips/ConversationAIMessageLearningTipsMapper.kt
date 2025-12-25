package com.ord.features.conversation.models.conversation_ai_message_learning_tips

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedCulturalTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedGrammarTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedIdiomTip
import com.ord.features.conversation.models.ai_message_tips.jsonb.AnnotatedVocabularyTip
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component

@Component
class ConversationAIMessageLearningTipsMapper :
    BidirectionalEntityMapper<ConversationAIMessageLearningTipsEntity, ConversationAIMessageLearningTipsDTO> {

    companion object {
        private val jsonObjectMapper = jacksonObjectMapper()
    }

    override fun toDTO(entity: ConversationAIMessageLearningTipsEntity): ConversationAIMessageLearningTipsDTO {
        return ConversationAIMessageLearningTipsDTO(
            id = entity.id ?: error("ConversationAIMessageLearningTips id must not be null"),
            grammarTips = deserializeGrammarTips(entity.grammarTips),
            vocabularyTips = deserializeVocabularyTips(entity.vocabularyTips),
            idiomTips = deserializeIdiomTips(entity.idiomTips),
            culturalTips = deserializeCulturalTips(entity.culturalTips),
            messageId = entity.messageId,
        )
    }

    override fun toEntity(dto: ConversationAIMessageLearningTipsDTO): ConversationAIMessageLearningTipsEntity {
        return ConversationAIMessageLearningTipsEntity(
            id = dto.id,
            grammarTips = serializeGrammarTips(dto.grammarTips),
            vocabularyTips = serializeVocabularyTips(dto.vocabularyTips),
            idiomTips = serializeIdiomTips(dto.idiomTips),
            culturalTips = serializeCulturalTips(dto.culturalTips),
            messageId = dto.messageId,
        )
    }

    fun serializeGrammarTips(tips: Set<AnnotatedGrammarTip>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(tips))
    }

    fun deserializeGrammarTips(json: Json): Set<AnnotatedGrammarTip> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<AnnotatedGrammarTip>>() {})
    }

    fun serializeVocabularyTips(tips: Set<AnnotatedVocabularyTip>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(tips))
    }

    fun deserializeVocabularyTips(json: Json): Set<AnnotatedVocabularyTip> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<AnnotatedVocabularyTip>>() {})
    }

    fun serializeIdiomTips(tips: Set<AnnotatedIdiomTip>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(tips))
    }

    fun deserializeIdiomTips(json: Json): Set<AnnotatedIdiomTip> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<AnnotatedIdiomTip>>() {})
    }

    fun serializeCulturalTips(tips: Set<AnnotatedCulturalTip>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(tips))
    }

    fun deserializeCulturalTips(json: Json): Set<AnnotatedCulturalTip> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<AnnotatedCulturalTip>>() {})
    }
}
