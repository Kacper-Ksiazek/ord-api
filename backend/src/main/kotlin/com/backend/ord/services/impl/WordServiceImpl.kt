package com.backend.ord.services.impl

import com.backend.ord.domain.entities.Word
import com.backend.ord.repositories.WordRepository
import com.backend.ord.services.WordService
import org.springframework.stereotype.Service
import java.util.*

@Service
class WordServiceImpl(
    private val wordRepository: WordRepository
) : WordService {
    override fun save(word: Word): Word {
        return wordRepository.save(word)
    }

    override fun findAllForUser(userId: UUID): List<Word> {
        return wordRepository.findAllByUserId(userId)
    }
}