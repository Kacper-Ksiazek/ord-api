package com.backend.ord.services.impl

import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.entities.UserSession
import com.backend.ord.exceptions.UserNotFoundException
import com.backend.ord.repositories.UserSessionRepository
import com.backend.ord.services.UserService
import com.backend.ord.services.UserSessionService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserSessionServiceImpl(
    private val userSessionRepository: UserSessionRepository,
    private val jwtService: JwtService,
    private val userService: UserService
) : UserSessionService {
    override fun save(userSession: UserSession?): UserSession? {
        return userSessionRepository.save(userSession)
    }

    override fun findByToken(token: String?): Optional<UserSession?>? {
        return userSessionRepository.findByToken(token)
    }

    override fun findByTokenAndUserId(token: String?, userId: UUID?): Optional<UserSession?>? {
        return userSessionRepository.findByTokenAndUserId(token, userId)
    }

    @Throws(UserNotFoundException::class)
    override fun openSessionFromJWT(token: String?) {
        val userId = jwtService.extractUserId(token)
        val user = userService.findById(userId)!!.orElseThrow { UserNotFoundException(userId) }!!

        userSessionRepository.save<UserSession>(
            UserSession.Companion.builder()
                .token(token)
                .user(user)
                .build()
        )
    }

    override fun deleteSessionByToken(authToken: String?) {
        userSessionRepository.deleteById(authToken)
    }
}
