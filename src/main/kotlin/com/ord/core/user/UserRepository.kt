package com.ord.core.user

import com.ord.core.user.model.UserEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?

    @Transactional
    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.email = :email")
    fun deleteByEmail(email: String)
}