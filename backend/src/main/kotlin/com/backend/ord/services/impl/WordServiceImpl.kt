package com.backend.ord.services.impl

import com.backend.ord.domain.entities.Word
import com.backend.ord.repositories.WordRepository
import com.backend.ord.services.WordService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class WordServiceImpl(
    override val repository: WordRepository
) : WordService {
    @Transactional
    override fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Word {
        return repository.changeBankForSingleWord(
            bankId = bankId,
            wordId = wordId,
            userId = userId
        )
    }

    override fun changeBankForMultipleWords(
        wordIds: List<UUID>,
        bankId: UUID?,
        userId: UUID
    ): List<Word> {
        return repository.changeBankForMultipleWords(
            bankId = bankId,
            wordIds = wordIds,
            userId = userId
        )
    }
}