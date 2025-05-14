package com.backend.ord.services

import com.backend.ord.core.auth.models.UserSessionEntity
import com.backend.ord.exceptions.UserNotFoundException
import java.util.*

interface UserSessionService {
    fun save(userSession: UserSessionEntity): UserSessionEntity

    fun findByToken(token: String): UserSessionEntity?

    fun findByTokenAndUserId(token: String, userId: UUID): UserSessionEntity?

    @Throws(UserNotFoundException::class)
    fun openSessionFromJWT(token: String)

    fun deleteSessionByToken(authToken: String)
}
