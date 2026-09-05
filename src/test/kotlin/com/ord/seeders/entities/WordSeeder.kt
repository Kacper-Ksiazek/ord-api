package com.ord.seeders.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.WordEntity
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
        return seedOneEntity(data, withProgress = true)
    }

    fun seedOneEntity(data: WordEntity?, withProgress: Boolean): WordEntity {
        val saved = wordRepository.save(data ?: wordMockFactory.mockEntity()).block()!!
        if (withProgress) {
            seedProgressForWords(listOf(saved))
        }
        return saved
    }

    override fun deleteAll() {
        wordProgressRepository.deleteAll().block()
        wordRepository.deleteAll().block()
    }

    fun saveMany(entities: List<WordEntity>, withProgress: Boolean = true): List<WordEntity> {
        val saved = wordRepository.saveAll(entities).collectList().block()!!
        if (withProgress) {
            seedProgressForWords(saved)
        }
        return saved
    }

    fun seedOneEntityForUser(
        userId: UUID,
        bankId: UUID? = null,
        language: LanguageName = LanguageName.ENGLISH,
        withProgress: Boolean = true,
    ): WordEntity {
        val mockEntity: WordEntity = wordMockFactory.mockEntity(userId = userId)
        mockEntity.language = language

        bankId?.let { mockEntity.bankId = it }

        return seedOneEntity(mockEntity, withProgress)
    }

    fun seedMultipleEntitiesForUser(
        userId: UUID,
        amount: Int = 5,
        language: LanguageName = LanguageName.ENGLISH,
        bankId: UUID? = null,
        withProgress: Boolean = true,
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

        return saveMany(wordEntities, withProgress)
    }

    private fun seedProgressForWords(words: List<WordEntity>) {
        val progressEntities = words
            .filter { it.id != null }
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
