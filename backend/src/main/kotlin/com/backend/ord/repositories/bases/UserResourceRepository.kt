package com.backend.ord.repositories.bases

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface UserResourceRepository<T> : JpaRepository<T, UUID> {
    @Query("SELECT r FROM #{#entityName} r WHERE r.user.id = :userId")
    fun findAllForUser(userId: UUID): List<T>

    @Query("SELECT r FROM #{#entityName} r WHERE r.user.id = :userId AND r.id = :id")
    fun findOneForUser(userId: UUID, id: UUID): T?

    fun findAllForUserBySearchingPhrase(userId: UUID, phrase: String): List<T> {
        throw NotImplementedError("This method should be implemented in the repository")
    }
}