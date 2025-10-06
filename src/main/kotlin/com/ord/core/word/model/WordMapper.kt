package com.ord.core.word.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.user.model.UserMapper
import com.ord.core.word.model.json.ExampleSentence
import com.ord.features.bank.model.BankMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component

@Component
class WordMapper(
    private val userMapper: UserMapper,
    private val bankMapper: BankMapper
) : BidirectionalEntityMapper<WordEntity, WordDTO> {
    private val objectMapper = jacksonObjectMapper()
    override fun toEntity(dto: WordDTO): WordEntity {
        return WordEntity(
            id = dto.id,

            type = dto.type,
            points = dto.points,
            origin = dto.origin,
            useCases = Json.of(objectMapper.writeValueAsString(dto.useCases)),
            extraMark = dto.extraMark,
            definition = dto.definition,
            translation = dto.translation,
            isCompleted = dto.isCompleted,
            isBookmarked = dto.isBookmarked,
            translatedFrom = dto.translatedFrom,
            translatedTo = dto.translatedTo,
            exampleSentences = Json.of(objectMapper.writeValueAsString(dto.exampleSentences)),

            userId = dto.userId,

            bankId = dto.bankId,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            completedAt = dto.completedAt
        )
    }

    override fun toDTO(entity: WordEntity): WordDTO {
        return WordDTO(
            id = entity.id ?: error("Word ID must not be null"),

            type = entity.type,
            points = entity.points,
            origin = entity.origin,
            useCases = objectMapper.readValue(entity.useCases.asString(), object : TypeReference<Set<String>>() {}),
            extraMark = entity.extraMark,
            definition = entity.definition,
            translation = entity.translation,
            isCompleted = entity.isCompleted,
            isBookmarked = entity.isBookmarked,
            translatedFrom = entity.translatedFrom,
            translatedTo = entity.translatedTo,
            exampleSentences = objectMapper.readValue(
                entity.exampleSentences.asString(),
                object : TypeReference<Set<ExampleSentence>>() {}),

            userId = entity.userId,

            bankId = entity.bankId,
            bank = null,

            bankGroupId = entity.bankGroupId,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            completedAt = entity.completedAt
        )
    }
}