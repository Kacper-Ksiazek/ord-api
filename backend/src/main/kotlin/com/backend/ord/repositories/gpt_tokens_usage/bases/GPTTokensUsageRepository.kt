package com.backend.ord.repositories.gpt_tokens_usage.bases

import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface GPTTokensUsageRepository<RepositoryType, ConsumptionType> : JpaRepository<RepositoryType, UUID> {
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
        SELECT new com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageStatistics(
            w.consumptionType, 
            w.translatedFrom, 
            COUNT(w.id),
            SUM(w.cost), 
            AVG(w.inputTokens), 
            AVG(w.outputTokens) 
        )
        FROM #{#entityName} w
        WHERE w.user.id = :userId
            AND EXTRACT(MONTH FROM w.createdAt) = :month
            AND EXTRACT(YEAR FROM w.createdAt) = :year
        GROUP BY w.consumptionType, w.translatedFrom
        ORDER BY COUNT(w.id) DESC
    """
    )
    fun findUsageStatsByUserInGivenMonth(
        userId: UUID,
        month: Int,
        year: Int
    ): List<TokensUsageStatistics<ConsumptionType>>
}