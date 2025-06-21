package com.ord.features.gpt_tokens_usage_log.variants.shared.repository

import com.ord.features.gpt_tokens_usage_log.api.dto.responses.dto.TokensUsageStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface TokensUsageRepository<RepositoryType, ConsumptionType> : JpaRepository<RepositoryType, UUID> {
    @Query(
        """
        SELECT r FROM #{#entityName} r 
        WHERE 
            r.user.id = :userId
        """
    )
    fun findAllForUser(userId: UUID): List<RepositoryType>

    @Query(
        """
        SELECT r FROM #{#entityName} r 
        WHERE 
            r.user.id = :userId 
            AND EXTRACT(MONTH FROM r.createdAt) = :month 
            AND EXTRACT(YEAR FROM r.createdAt) = :year
        """
    )
    fun findAllByUserInGivenMonth(
        userId: UUID,
        month: Int,
        year: Int
    ): List<RepositoryType>

    @Query(
        """
        SELECT new com.ord.features.gpt_tokens_usage_log.api.dto.responses.dto.TokensUsageStatistics(
            w.consumptionType, 
            w.language, 
            COUNT(w.id),
            SUM(w.cost), 
            AVG(w.inputTokens), 
            AVG(w.outputTokens) 
        )
        FROM #{#entityName} w
        WHERE w.user.id = :userId
            AND EXTRACT(MONTH FROM w.createdAt) = :month
            AND EXTRACT(YEAR FROM w.createdAt) = :year
        GROUP BY w.consumptionType, w.language
        ORDER BY COUNT(w.id) DESC
    """
    )
    fun findUsageStatsByUserInGivenMonth(
        userId: UUID,
        month: Int,
        year: Int
    ): List<TokensUsageStatistics<ConsumptionType>>
}