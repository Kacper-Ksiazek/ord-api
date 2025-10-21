package com.ord.core.word.services

import com.ord.core.word.models.word_details.WordDetailsDTO
import com.ord.core.word.models.word_details.WordDetailsEntity
import com.ord.shared.services.UserResourceService
import reactor.core.publisher.Mono
import java.util.*

interface WordDetailsService : UserResourceService<WordDetailsEntity> {
    fun createWordDetails(
        wordId: UUID,
        wordDetailsDTO: WordDetailsDTO,
        userId: UUID
    ): Mono<WordDetailsDTO>

    fun updateWordDetails(
        wordId: UUID,
        wordDetailsDTO: WordDetailsDTO,
        userId: UUID
    ): Mono<WordDetailsDTO>

    fun getWordDetailsByWordId(
        wordId: UUID,
        userId: UUID
    ): Mono<WordDetailsDTO>

    fun deleteWordDetailsByWordId(
        wordId: UUID,
        userId: UUID
    ): Mono<Void>
}