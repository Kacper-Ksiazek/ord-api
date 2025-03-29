package com.backend.ord.repositories

import com.backend.ord.domain.persistence.entities.UserSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserSessionRepository : JpaRepository<UserSession, String> {
    fun findByToken(token: String): UserSession?

    @Query("SELECT us FROM UserSession us WHERE us.token = :token AND us.user.id = :userId")
    fun findByTokenAndUserId(token: String, userId: UUID): UserSession?

    @Modifying
    @Query("DELETE FROM UserSession us WHERE us.token = :token")
    fun deleteByToken(token: String): Unit
}
