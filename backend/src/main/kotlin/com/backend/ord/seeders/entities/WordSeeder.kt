package com.backend.ord.seeders.entities

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.utils.Optional
import org.springframework.stereotype.Component

@Component
class WordSeeder(
    private val userMapper: UserMapper,
    private val wordMockFactory: WordMockFactory,
    private val wordRepository: WordRepository
) : SeederInterface<Word> {
    override fun seedOneEntity(data: Word?): Word {
        return wordRepository.save(data ?: wordMockFactory.mockEntity())
    }

    override fun deleteAll() {
        wordRepository.deleteAll()
    }

    fun saveMany(entities: List<Word>): List<Word> {
        return wordRepository.saveAll(entities)
    }

    fun seedOneEntityForUser(
        user: UserEntity,
        bank: Optional<Bank?> = Optional(null, false),
        language: LanguageName = LanguageName.ENGLISH
    ): Word {
        val mockEntity: Word = wordMockFactory.mockEntity(user = user)
        mockEntity.translatedFrom = language

        if (bank.isPresent) mockEntity.bank = bank.value

        return wordRepository.save(mockEntity)
    }

    fun seedOneEntityForUser(
        user: UserDTO,
        bank: Optional<Bank?> = Optional(null, false)
    ): Word {
        return seedOneEntityForUser(
            user = userMapper.toEntity(user),
            bank = bank
        )
    }

    fun seedMultipleEntitiesForUser(
        user: UserEntity,
        amount: Int = 5,
        bank: Optional<Bank?> = Optional(null, false),
        language: LanguageName = LanguageName.ENGLISH
    ): List<Word> {
        val words = mutableListOf<Word>()

        repeat(amount) {
            words.add(
                wordMockFactory.mockEntity(
                    user = user,
                    bank = if (bank.isPresent) bank.value else null,
                    translatedFrom = language
                )
            )
        }

        return wordRepository.saveAll(words)
    }

    fun seedMultipleEntitiesForUser(
        user: UserDTO,
        amount: Int = 5,
        bank: Optional<Bank?> = Optional(null, false),
        language: LanguageName = LanguageName.ENGLISH
    ): List<Word> {
        return seedMultipleEntitiesForUser(
            user = userMapper.toEntity(user),
            amount = amount,
            bank = bank,
            language = language
        )
    }
}
