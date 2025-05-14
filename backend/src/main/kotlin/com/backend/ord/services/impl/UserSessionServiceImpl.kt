package com.backend.ord.services.impl

import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.core.auth.models.UserSessionEntity
import com.backend.ord.core.user.service.UserService
import com.backend.ord.exceptions.UserNotFoundException
import com.backend.ord.repositories.UserSessionRepository
import com.backend.ord.services.UserSessionService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserSessionServiceImpl(
    private val userSessionRepository: UserSessionRepository,
    private val jwtService: JwtService,
    private val userService: UserService
) : UserSessionService {
    override fun save(userSession: UserSessionEntity): UserSessionEntity {
        return userSessionRepository.save(userSession)
    }

    override fun findByToken(token: String): UserSessionEntity? {
        return userSessionRepository.findByToken(token)
    }

    override fun findByTokenAndUserId(token: String, userId: UUID): UserSessionEntity? {
        return userSessionRepository.findByTokenAndUserId(token, userId)
    }

    @Throws(UserNotFoundException::class)
    override fun openSessionFromJWT(token: String) {
        val userId = jwtService.extractUserId(token)
        val user = userService.findById(userId) ?: throw UserNotFoundException(userId = userId)

        userSessionRepository.save(
            UserSessionEntity(
                token = token,
                user = user
            )
        )
    }

    @Transactional
    override fun deleteSessionByToken(authToken: String) {
        userSessionRepository.deleteByToken(authToken)
    }
}
