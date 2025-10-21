package com.ord.core.word.services.impl

import com.ord.core.word.models.word_details.WordDetailsDTO
import com.ord.core.word.models.word_details.WordDetailsEntity
import com.ord.core.word.models.word_details.WordDetailsMapper
import com.ord.core.word.repositories.WordDetailsRepository
import com.ord.core.word.services.WordDetailsService
import com.ord.exceptions.REST.ConflictException
import com.ord.exceptions.REST.NotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class WordDetailsServiceImpl(
    private val wordDetailsRepository: WordDetailsRepository,
    private val wordDetailsMapper: WordDetailsMapper
) : WordDetailsService {
    override val repository: WordDetailsRepository = wordDetailsRepository

    override fun createWordDetails(
        wordId: UUID,
        wordDetailsDTO: WordDetailsDTO,
        userId: UUID
    ): Mono<WordDetailsDTO> {
        return wordDetailsRepository
            .existsByWordIdAndUserId(
                wordId = wordId,
                userId = userId
            )
            .flatMap { exists ->
                if (exists) {
                    Mono.error(ConflictException("Word details already exist for word with id $wordId"))
                } else {
                    val entity = wordDetailsMapper.toEntity(
                        wordDetailsDTO.copy(
                            id = UUID.randomUUID(),
                            wordId = wordId,
                            userId = userId,
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                    )
                    wordDetailsRepository
                        .save(entity)
                        .map { wordDetailsMapper.toDTO(it) }
                }
            }
    }

    override fun updateWordDetails(
        wordId: UUID,
        wordDetailsDTO: WordDetailsDTO,
        userId: UUID
    ): Mono<WordDetailsDTO> {
        return wordDetailsRepository
            .findByWordIdAndUserId(
                wordId = wordId,
                userId = userId
            )
            .switchIfEmpty(
                Mono.error(NotFoundException("Word details not found for word with id $wordId"))
            )
            .flatMap { existing ->
                val updated = wordDetailsMapper.toEntity(
                    wordDetailsDTO.copy(
                        id = existing.id!!,
                        wordId = wordId,
                        userId = userId,
                        createdAt = existing.createdAt,
                        updatedAt = Instant.now()
                    )
                )
                wordDetailsRepository
                    .save(updated)
                    .map { wordDetailsMapper.toDTO(it) }
            }
    }

    override fun getWordDetailsByWordId(
        wordId: UUID,
        userId: UUID
    ): Mono<WordDetailsDTO> {
        return wordDetailsRepository
            .findByWordIdAndUserId(
                wordId = wordId,
                userId = userId
            )
            .switchIfEmpty(
                Mono.error(NotFoundException("Word details not found for word with id $wordId"))
            )
            .map { wordDetailsMapper.toDTO(it) }
    }

    override fun deleteWordDetailsByWordId(
        wordId: UUID,
        userId: UUID
    ): Mono<Void> {
        return wordDetailsRepository.deleteByWordIdAndUserId(
            wordId = wordId,
            userId = userId
        )
    }
}