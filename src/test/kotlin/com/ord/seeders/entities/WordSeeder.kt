package com.ord.seeders.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.UserMapper
import com.ord.core.word.model.WordEntity
import com.ord.core.word.repository.WordRepository
import com.ord.features.bank.model.BankEntity
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.seeders.factories.WordFactory
import com.ord.shared.utils.data_classes.Optional
import org.springframework.stereotype.Component

@Component
class WordSeeder(
    private val userMapper: UserMapper,
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
        user: UserEntity,
        bank: Optional<BankEntity?> = Optional(null, false),
        language: LanguageName = LanguageName.ENGLISH
    ): WordEntity {
        val mockEntity: WordEntity = wordMockFactory.mockEntity(user = user)
        mockEntity.translatedFrom = language

        if (bank.isPresent) mockEntity.bank = bank.value

        return wordRepository.save(mockEntity).block()!!
    }

    fun seedOneEntityForUser(
        user: UserDTO,
        bank: Optional<BankEntity?> = Optional(null, false)
    ): WordEntity {
        return seedOneEntityForUser(
            user = userMapper.toEntity(user),
            bank = bank
        )
    }

    fun seedMultipleEntitiesForUser(
        user: UserEntity,
        amount: Int = 5,
        bank: Optional<BankEntity?> = Optional(null, false),
        language: LanguageName = LanguageName.ENGLISH
    ): List<WordEntity> {
        val wordEntities = mutableListOf<WordEntity>()

        repeat(amount) {
            wordEntities.add(
                wordMockFactory.mockEntity(
                    user = user,
                    bank = if (bank.isPresent) bank.value else null,
                    translatedFrom = language
                )
            )
        }

        return wordRepository.saveAll(wordEntities).collectList().block()!!
    }

    fun seedMultipleEntitiesForUser(
        user: UserDTO,
        amount: Int = 5,
        bank: Optional<BankEntity?> = Optional(null, false),
        language: LanguageName = LanguageName.ENGLISH
    ): List<WordEntity> {
        return seedMultipleEntitiesForUser(
            user = userMapper.toEntity(user),
            amount = amount,
            bank = bank,
            language = language
        )
    }
}