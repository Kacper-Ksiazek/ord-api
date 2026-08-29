package com.ord.seeders.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.core.word.repositories.WordProgressRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.seeders.factories.WordFactory
import com.ord.seeders.factories.WordProgressFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordSeeder(
    private val wordMockFactory: WordFactory,
    private val wordProgressFactory: WordProgressFactory,
    private val wordRepository: WordRepository,
    private val wordProgressRepository: WordProgressRepository,
) : SeederInterface<WordEntity> {
    override fun seedOneEntity(data: WordEntity?): WordEntity {
        val saved = wordRepository.save(data ?: wordMockFactory.mockEntity()).block()!!
        seedProgressForActiveWords(listOf(saved))
        return saved
    }

    override fun deleteAll() {
        wordProgressRepository.deleteAll().block()
        wordRepository.deleteAll().block()
    }

    fun saveMany(entities: List<WordEntity>): List<WordEntity> {
        val saved = wordRepository.saveAll(entities).collectList().block()!!
        seedProgressForActiveWords(saved)
        return saved
    }

    fun seedOneEntityForUser(
        userId: UUID,
        bankId: UUID? = null,
        language: LanguageName = LanguageName.ENGLISH,
    ): WordEntity {
        val mockEntity: WordEntity = wordMockFactory.mockEntity(userId = userId)
        mockEntity.language = language

        bankId?.let { mockEntity.bankId = it }

        return seedOneEntity(mockEntity)
    }

    fun seedMultipleEntitiesForUser(
        userId: UUID,
        amount: Int = 5,
        language: LanguageName = LanguageName.ENGLISH,
        bankId: UUID? = null,
    ): List<WordEntity> {
        val wordEntities = mutableListOf<WordEntity>()

        repeat(amount) {
            wordEntities.add(
                wordMockFactory.mockEntity(
                    userId = userId,
                    bankId = bankId,
                    language = language,
                ),
            )
        }

        return saveMany(wordEntities)
    }

    private fun seedProgressForActiveWords(words: List<WordEntity>) {
        val progressEntities = words
            .filter { it.status == WordStatus.ACTIVE && it.id != null }
            .map { word ->
                wordProgressFactory.mockEntity(
                    wordId = word.id!!,
                    userId = word.userId,
                )
            }

        if (progressEntities.isNotEmpty()) {
            wordProgressRepository.saveAll(progressEntities).collectList().block()
        }
    }

    fun seedProgress(
        wordId: UUID,
        userId: UUID,
        points: Int = 0,
    ): WordProgressEntity {
        return wordProgressRepository.save(
            wordProgressFactory.mockEntity(
                wordId = wordId,
                userId = userId,
                points = points,
            ),
        ).block()!!
    }
}
