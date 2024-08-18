package com.backend.ord.seeders.entities

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
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

    fun seedOneEntityForUser(user: User): Word {
        return wordRepository.save(wordMockFactory.mockEntity(user = user))
    }

    fun seedOneEntityForUser(user: UserDTO): Word {
        return seedOneEntityForUser(user = userMapper.toEntity(user))
    }

    override fun deleteAll() {
        wordRepository.deleteAll()
    }
}
