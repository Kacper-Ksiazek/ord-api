package com.backend.ord.services

import com.backend.ord.domain.entities.Word
import java.util.*

interface WordService {
    fun save(word: Word): Word;

    fun findAllForUser(userId: UUID): List<Word>;
}