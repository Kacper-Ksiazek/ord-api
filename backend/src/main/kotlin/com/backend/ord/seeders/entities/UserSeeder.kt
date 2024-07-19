package com.backend.ord.seeders.entities

import com.backend.ord.domain.entities.User
import com.backend.ord.repositories.UserRepository
import com.backend.ord.seeders.factories.UserMockFactory
import org.springframework.stereotype.Component

@Component
class UserSeeder(
    private val userRepository: UserRepository,
    private val userMockFactory: UserMockFactory
) :
    SeederInterface<User> {
    override fun insertRow(): User {
        return userRepository.save(userMockFactory.mockEntity())
    }

    fun insertRowWithCredentials(email: String, password: String): User {
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
