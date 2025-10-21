package com.ord.core.word.models.word_details

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class WordDetailsMapper : BidirectionalEntityMapper<WordDetailsEntity, WordDetailsDTO> {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toEntity(dto: WordDetailsDTO): WordDetailsEntity {
        return WordDetailsEntity(
            id = dto.id,
            wordId = dto.wordId,
            useCases = serializeStringSet(dto.useCases),
            synonyms = serializeStringSet(dto.synonyms),
            antonyms = serializeStringSet(dto.antonyms),
            commonMistakes = serializeStringSet(dto.commonMistakes),
            exampleSentences = serializeExampleSentences(dto.exampleSentences),
            collocations = serializeCollocations(dto.collocations),
            pronunciation = dto.pronunciation?.let { jsonObjectMapper.writeValueAsString(it) },
            grammar = dto.grammar?.let { jsonObjectMapper.writeValueAsString(it) },
            culturalNotes = dto.culturalNotes,
            learningTips = dto.learningTips,
            userId = dto.userId,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: WordDetailsEntity): WordDetailsDTO {
        return WordDetailsDTO(
            id = entity.id ?: error("WordDetails ID must not be null"),
            wordId = entity.wordId,
            useCases = deserializeStringSet(entity.useCases),
            synonyms = deserializeStringSet(entity.synonyms),
            antonyms = deserializeStringSet(entity.antonyms),
            commonMistakes = deserializeStringSet(entity.commonMistakes),
            exampleSentences = deserializeExampleSentences(entity.exampleSentences),
            collocations = deserializeCollocations(entity.collocations),
            pronunciation = entity.pronunciation?.let {
                jsonObjectMapper.readValue(it, WordPronunciation::class.java)
            },
            grammar = entity.grammar?.let {
                jsonObjectMapper.readValue(it, WordGrammar::class.java)
            },
            culturalNotes = entity.culturalNotes,
            learningTips = entity.learningTips,
            userId = entity.userId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun serializeStringSet(set: Set<String>): String {
        return jsonObjectMapper.writeValueAsString(set)
    }

    fun deserializeStringSet(json: String): Set<String> {
        return jsonObjectMapper.readValue(json, object : TypeReference<Set<String>>() {})
    }

    fun serializeExampleSentences(sentences: Set<ExampleSentence>): String {
        return jsonObjectMapper.writeValueAsString(sentences)
    }

    fun deserializeExampleSentences(json: String): Set<ExampleSentence> {
        return jsonObjectMapper.readValue(json, object : TypeReference<Set<ExampleSentence>>() {})
    }

    fun serializeCollocations(collocations: Set<WordCollocation>): String {
        return jsonObjectMapper.writeValueAsString(collocations)
    }

    fun deserializeCollocations(json: String): Set<WordCollocation> {
        return jsonObjectMapper.readValue(json, object : TypeReference<Set<WordCollocation>>() {})
    }
}
