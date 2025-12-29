package com.ord.features.conversation.models.conversation_user_message_feedback

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.AlternativeExpression
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.VocabularyEnrichment
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component

@Component
class ConversationUserMessageFeedbackMapper(
) : BidirectionalEntityMapper<ConversationUserMessageFeedbackEntity, ConversationUserMessageFeedbackDTO> {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toDTO(entity: ConversationUserMessageFeedbackEntity): ConversationUserMessageFeedbackDTO {
        return ConversationUserMessageFeedbackDTO(
            id = entity.id ?: error("ConversationUserMessageFeedback id must not be null"),
            tutorComment = entity.tutorComment,
            grammar = entity.grammar,
            vocabulary = entity.vocabulary,
            answerLength = entity.answerLength,
            naturalness = entity.naturalness,
            coherenceWithContext = entity.coherenceWithContext,
            registerAppropriate = entity.registerAppropriate,
            mistakes = deserializeMistakes(entity.mistakes),
            strengthsIdentified = deserializeStringSet(entity.strengthsIdentified),
            vocabularyEnrichment = deserializeVocabularyEnrichment(entity.vocabularyEnrichment),
            alternativeExpressions = deserializeAlternativeExpressions(entity.alternativeExpressions),
            culturalNote = entity.culturalNote,
            messageId = entity.messageId,
        )
    }

    override fun toEntity(dto: ConversationUserMessageFeedbackDTO): ConversationUserMessageFeedbackEntity {
        return ConversationUserMessageFeedbackEntity(
            id = dto.id,
            tutorComment = dto.tutorComment,
            grammar = dto.grammar,
            vocabulary = dto.vocabulary,
            answerLength = dto.answerLength,
            naturalness = dto.naturalness,
            coherenceWithContext = dto.coherenceWithContext,
            registerAppropriate = dto.registerAppropriate,
            mistakes = serializeMistakes(dto.mistakes),
            strengthsIdentified = serializeStringSet(dto.strengthsIdentified),
            vocabularyEnrichment = serializeVocabularyEnrichment(dto.vocabularyEnrichment),
            alternativeExpressions = serializeAlternativeExpressions(dto.alternativeExpressions),
            culturalNote = dto.culturalNote,
            messageId = dto.messageId,
        )
    }

    fun serializeStringSet(set: Set<String>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(set))
    }

    fun deserializeStringSet(json: Json): Set<String> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<String>>() {})
    }

    fun serializeMistakes(mistakes: Set<Mistake>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(mistakes))
    }

    fun deserializeMistakes(json: Json): Set<Mistake> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<Mistake>>() {})
    }

    fun serializeVocabularyEnrichment(enrichment: Set<VocabularyEnrichment>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(enrichment))
    }

    fun deserializeVocabularyEnrichment(json: Json): Set<VocabularyEnrichment> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<VocabularyEnrichment>>() {})
    }

    fun serializeAlternativeExpressions(expressions: Set<AlternativeExpression>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(expressions))
    }

    fun deserializeAlternativeExpressions(json: Json): Set<AlternativeExpression> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<AlternativeExpression>>() {})
    }
}