package com.ord.features.conversation.models.conversation_user_message_feedback

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Mistake
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Strength
import com.ord.features.conversation.models.conversation_user_message_feedback.jsonb.Suggestion
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
            strengthsIdentified = deserializeStrengths(entity.strengthsIdentified),
            suggestions = deserializeSuggestions(entity.suggestions),
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
            strengthsIdentified = serializeStrengths(dto.strengthsIdentified),
            suggestions = serializeSuggestions(dto.suggestions),
            messageId = dto.messageId,
        )
    }

    fun serializeMistakes(mistakes: Set<Mistake>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(mistakes))
    }

    fun deserializeMistakes(json: Json): Set<Mistake> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<Mistake>>() {})
    }

    fun serializeStrengths(strengths: Set<Strength>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(strengths))
    }

    fun deserializeStrengths(json: Json): Set<Strength> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<Strength>>() {})
    }

    fun serializeSuggestions(suggestions: Set<Suggestion>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(suggestions))
    }

    fun deserializeSuggestions(json: Json): Set<Suggestion> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<Suggestion>>() {})
    }
}
