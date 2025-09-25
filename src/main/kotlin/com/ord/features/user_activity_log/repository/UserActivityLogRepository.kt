package com.ord.features.user_activity_log.repository

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface UserActivityLogRepository :
    UserResourceRepository<UserActivityLogEntity>,
    ReactiveCrudRepository<UserActivityLogEntity, UUID> {
    @Query(
        """
        SELECT COUNT(*) FROM user_activity_logs
        WHERE 
            user_id = :userId
            AND type = :type
            AND language = :language
            AND DATE(created_at) = CURRENT_DATE
        """
    )
    fun countDailyLog(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName
    ): Mono<Long>


    @Query(
        """
        SELECT COUNT(*) FROM user_activity_logs
        WHERE 
            user_id = :userId
            AND type = :type
            AND language = :language
            AND DATE(created_at) >= DATE(CURRENT_DATE - INTERVAL '7 days')
            AND DATE(created_at) <= CURRENT_DATE
        """
    )
    fun countWeeklyLog(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName
    ): Mono<Long>


    @Query(
        """
        SELECT COUNT(*) FROM user_activity_logs
        WHERE 
            user_id = :userId
            AND type = :type
            AND language = :language
            AND DATE(created_at) >= DATE(CURRENT_DATE - INTERVAL '30 days')
            AND DATE(created_at) <= CURRENT_DATE
        """
    )
    fun countMonthlyLog(
        userId: UUID,
        type: UserActivityType,
        language: LanguageName
    ): Mono<Long>
}