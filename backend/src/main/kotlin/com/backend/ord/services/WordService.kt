package com.backend.ord.services

import com.backend.ord.domain.entities.Word
import com.backend.ord.services.bases.UserResourceService
import java.util.UUID

interface WordService : UserResourceService<Word> {
    fun changeBankForSingleWord(
        wordId: UUID,
        bankId: UUID?,
        userId: UUID
    ): Word

    fun changeBankForMultipleWords(
        wordIds: List<UUID>,
        bankId: UUID?,
        userId: UUID
    ): List<Word>
}