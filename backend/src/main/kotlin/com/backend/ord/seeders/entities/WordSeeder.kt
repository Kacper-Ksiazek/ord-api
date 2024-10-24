package com.backend.ord.seeders.entities

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.UserMapper
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
        user: User,
        bank: Optional<Bank?> = Optional(null, false)
    ): Word {
        val mockEntity = wordMockFactory.mockEntity(user = user)

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
        user: User,
        amount: Int = 5,
        bank: Optional<Bank?> = Optional(null, false)
    ): List<Word> {
        val words = mutableListOf<Word>()

        for (i in 1..amount) {
            words.add(seedOneEntityForUser(user, bank))
        }

        return words
    }

    fun seedMultipleEntitiesForUser(
        user: UserDTO,
        amount: Int = 5,
        bank: Optional<Bank?> = Optional(null, false)
    ): List<Word> {
        return seedMultipleEntitiesForUser(
            user = userMapper.toEntity(user),
            amount = amount,
            bank = bank
        )
    }
}
