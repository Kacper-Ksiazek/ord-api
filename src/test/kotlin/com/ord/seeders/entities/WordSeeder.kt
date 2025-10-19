package com.ord.seeders.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.repositories.WordRepository
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.seeders.factories.WordFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordSeeder(
    private val wordMockFactory: WordFactory,
    private val wordRepository: WordRepository
) : SeederInterface<WordEntity> {
    override fun seedOneEntity(data: WordEntity?): WordEntity {
        return wordRepository.save(data ?: wordMockFactory.mockEntity()).block()!!
    }

    override fun deleteAll() {
        wordRepository.deleteAll().block()
    }

    fun saveMany(entities: List<WordEntity>): List<WordEntity> {
        return wordRepository.saveAll(entities).collectList().block()!!
    }

    fun seedOneEntityForUser(
        userId: UUID,
        bankId: UUID? = null,
        language: LanguageName = LanguageName.ENGLISH
    ): WordEntity {
        val mockEntity: WordEntity = wordMockFactory.mockEntity(userId = userId)
        mockEntity.translatedFrom = language

        bankId?.let { mockEntity.bankId = it }

        return wordRepository.save(mockEntity).block()!!
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
                    translatedFrom = language
                )
            )
        }

        return wordRepository.saveAll(wordEntities).collectList().block()!!
    }
}