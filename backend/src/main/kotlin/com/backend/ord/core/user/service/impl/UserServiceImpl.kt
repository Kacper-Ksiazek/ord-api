package com.backend.ord.core.user.service.impl

import com.backend.ord.core.user.UserRepository
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.service.UserService
import com.backend.ord.repositories.UserSessionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userSessionRepository: UserSessionRepository
) : UserService {
    override fun findAll(): List<UserEntity> {
        return userRepository.findAll().toList().filterNotNull()
    }

    override fun findById(id: UUID): UserEntity? {
        return userRepository.findByIdOrNull(id)
    }

    override fun save(user: UserEntity): UserEntity {
        return userRepository.save(user)
    }

    override fun findUserByAuthToken(authToken: String): UserEntity? {
        return userSessionRepository.findByToken(authToken)?.user
    }

    override fun findUserByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }
}