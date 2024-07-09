package com.backend.ord.services

import com.backend.ord.domain.entities.UserSession
import com.backend.ord.exceptions.UserNotFoundException
import java.util.*

interface UserSessionService {
    fun save(userSession: UserSession?): UserSession?

    fun findByToken(token: String?): Optional<UserSession?>?

    fun findByTokenAndUserId(token: String?, userId: UUID?): Optional<UserSession?>?

    @Throws(UserNotFoundException::class)
    fun openSessionFromJWT(token: String?)

    fun deleteSessionByToken(authToken: String?)
}
