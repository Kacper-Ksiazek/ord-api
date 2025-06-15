package com.backend.ord.seeders.entities

import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.seeders.entities.bases.SeederInterface
import com.backend.ord.seeders.factories.UserFactory
import org.springframework.stereotype.Component

@Component
class UserSeeder(
    private val userRepository: UserRepository,
    private val userMockFactory: UserFactory
) : SeederInterface<UserEntity> {
    override fun seedOneEntity(data: UserEntity?): UserEntity {
        return userRepository.save(data ?: userMockFactory.mockEntity())
    }

    fun insertRowWithCredentials(email: String, password: String): UserEntity {
        return userRepository.save(
            userMockFactory.mockEntity(
                email = email,
                password = password
            )
        )
    }

    override fun deleteAll() {
        userRepository.deleteAll()
    }
}
