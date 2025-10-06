package com.ord.seeders.entities

import com.ord.core.security.UserRepository
import com.ord.core.user.model.UserEntity
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.seeders.factories.UserFactory
import org.springframework.stereotype.Component

@Component
class UserSeeder(
    private val userRepository: UserRepository,
    private val userMockFactory: UserFactory
) : SeederInterface<UserEntity> {
    override fun seedOneEntity(data: UserEntity?): UserEntity {
        return userRepository.save(data ?: userMockFactory.mockEntity()).block()!!
    }

    fun insertRowWithCredentials(email: String, password: String): UserEntity {
        return userRepository.save(
            userMockFactory.mockEntity(
                email = email,
                password = password
            )
        ).block()!!
    }

    override fun deleteAll() {
        userRepository.deleteAll().block()
    }
}
