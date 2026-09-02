package com.ord.core.word.models.word_progress

import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordProgressMapper : BidirectionalEntityMapper<WordProgressEntity, WordProgressDTO> {
    override fun toEntity(dto: WordProgressDTO): WordProgressEntity {
        error("WordProgressDTO cannot be mapped to entity without wordId and userId")
    }

    fun toInitialEntity(wordId: UUID, userId: UUID): WordProgressEntity {
        return WordProgressEntity(
            wordId = wordId,
            userId = userId,
        )
    }

    override fun toDTO(entity: WordProgressEntity): WordProgressDTO {
        return WordProgressDTO.fromEntity(entity)
    }
}
