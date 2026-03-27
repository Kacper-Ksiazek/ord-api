package com.ord.features.conversation.models.conversation_user_message_analysis

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageMistake
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageStrength
import com.ord.features.conversation.models.conversation_user_message_analysis.jsonb.ConversationMessageSuggestion
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component

@Component
class ConversationUserMessageAnalysisMapper(
) : BidirectionalEntityMapper<ConversationUserMessageAnalysisEntity, ConversationUserMessageAnalysisDTO> {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toDTO(entity: ConversationUserMessageAnalysisEntity): ConversationUserMessageAnalysisDTO {
        return ConversationUserMessageAnalysisDTO(
            id = entity.id ?: error("ConversationUserMessageAnalysis id must not be null"),
            tutorComment = entity.tutorComment,
            correctedMessage = entity.correctedMessage,
            grammar = entity.grammar,
            vocabulary = entity.vocabulary,
            naturalness = entity.naturalness,
            coherenceWithContext = entity.coherenceWithContext,
            mistakes = deserializeMistakes(entity.mistakes),
            strengths = deserializeStrengths(entity.strengths),
            suggestions = deserializeSuggestions(entity.suggestions),
            messageId = entity.messageId,
        )
    }

    override fun toEntity(dto: ConversationUserMessageAnalysisDTO): ConversationUserMessageAnalysisEntity {
        return ConversationUserMessageAnalysisEntity(
            id = dto.id,
            tutorComment = dto.tutorComment,
            correctedMessage = dto.correctedMessage,
            grammar = dto.grammar,
            vocabulary = dto.vocabulary,
            naturalness = dto.naturalness,
            coherenceWithContext = dto.coherenceWithContext,
            mistakes = serializeMistakes(dto.mistakes),
            strengths = serializeStrengths(dto.strengths),
            suggestions = serializeSuggestions(dto.suggestions),
            messageId = dto.messageId,
        )
    }

    fun serializeMistakes(mistakes: Set<ConversationMessageMistake>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(mistakes))
    }

    fun deserializeMistakes(json: Json): Set<ConversationMessageMistake> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<ConversationMessageMistake>>() {})
    }

    fun serializeStrengths(strengths: Set<ConversationMessageStrength>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(strengths))
    }

    fun deserializeStrengths(json: Json): Set<ConversationMessageStrength> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<ConversationMessageStrength>>() {})
    }

    fun serializeSuggestions(suggestions: Set<ConversationMessageSuggestion>): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(suggestions))
    }

    fun deserializeSuggestions(json: Json): Set<ConversationMessageSuggestion> {
        return jsonObjectMapper.readValue(json.asString(), object : TypeReference<Set<ConversationMessageSuggestion>>() {})
    }
}
