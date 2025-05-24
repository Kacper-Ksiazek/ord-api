package com.backend.ord.core.auth.repositories

import com.backend.ord.core.auth.models.UserSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserSessionRepository : JpaRepository<UserSessionEntity, String> {
    fun findByToken(token: String): UserSessionEntity?

    @Query("SELECT us FROM UserSessionEntity us WHERE us.token = :token AND us.user.id = :userId")
    fun findByTokenAndUserId(token: String, userId: UUID): UserSessionEntity?

    @Modifying
    @Query("DELETE FROM UserSessionEntity us WHERE us.token = :token")
    fun deleteByToken(token: String)
}