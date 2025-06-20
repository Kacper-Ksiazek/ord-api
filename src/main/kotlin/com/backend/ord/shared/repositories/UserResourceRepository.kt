package com.backend.ord.shared.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface UserResourceRepository<T> : JpaRepository<T, UUID> {
    @Query("SELECT r FROM #{#entityName} r WHERE r.user.id = :userId")
    fun findAllForUser(userId: UUID): List<T>

    @Query("SELECT r FROM #{#entityName} r WHERE r.user.id = :userId AND r.id IN :ids")
    fun findAllForUser(ids: Set<UUID>, userId: UUID): List<T>

    @Query("SELECT r FROM #{#entityName} r WHERE r.user.id = :userId AND r.id = :id")
    fun findOneForUser(userId: UUID, id: UUID): T?

    @Modifying
    @Query("DELETE FROM #{#entityName} r WHERE r.user.id = :userId AND r.id = :id")
    fun deleteOneForUser(userId: UUID, id: UUID): Int
}