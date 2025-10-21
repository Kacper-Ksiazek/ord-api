package com.ord.core.word.api.details.facades

import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word_details.WordDetailsDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.*

interface WordDetailsFacade {
    fun createWordDetails(
        wordId: UUID,
        request: CreateWordDetailsRequest,
        userId: UUID
    ): Mono<ResponseEntity<WordDetailsDTO>>

    
    fun updateWordDetails(
        wordId: UUID,
        request: UpdateWordDetailsRequest,
        userId: UUID
    ): Mono<ResponseEntity<WordDetailsDTO>>
}