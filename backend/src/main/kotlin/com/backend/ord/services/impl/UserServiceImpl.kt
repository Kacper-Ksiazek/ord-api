package com.backend.ord.services.impl

import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.repositories.UserRepository
import com.backend.ord.repositories.UserSessionRepository
import com.backend.ord.services.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userSessionRepository: UserSessionRepository
) : UserService {
    override fun findAll(): List<User> {
        return userRepository.findAll().toList().filterNotNull()
    }

    override fun findById(id: UUID): User? {
        return userRepository.findByIdOrNull(id)
    }

    override fun save(user: User): User {
        return userRepository.save(user)
    }

    override fun findUserByAuthToken(authToken: String): User? {
        return userSessionRepository.findByToken(authToken)?.user
    }

    override fun findUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}
