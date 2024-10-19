package com.backend.ord.repositories.gpt_tokens_usage.bases

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface GPTTokensUsageRepository<T> : JpaRepository<T, UUID> {
    @Query("SELECT r FROM #{#entityName} r WHERE r.user.id = :userId AND EXTRACT(MONTH FROM r.createdAt) = :month AND EXTRACT(YEAR FROM r.createdAt) = :year")
    fun findAllByUserInGivenMonth(
        userId: UUID,
        month: Int,
        year: Int
    ): List<T>
}