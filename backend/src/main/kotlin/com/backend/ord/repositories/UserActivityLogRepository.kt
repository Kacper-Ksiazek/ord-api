package com.backend.ord.repositories

import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserActivityLogRepository : UserResourceRepository<UserActivityLog> {
    @Query(
        """
        SELECT COUNT(u) FROM UserActivityLog u
        WHERE 
            u.user.id = :userId
            AND u.type = :type
            AND u.language = :language
            AND date_trunc('day', u.createdAt) = date_trunc('day', CURRENT_DATE)
    """
    )
    fun countDailyLog(userId: UUID, type: UserActivityType, language: LanguageName): Int

    @Query(
        """
        SELECT COUNT(u) FROM UserActivityLog u
        WHERE 
            u.user.id = :userId
            AND u.type = :type
            AND u.language = :language
            AND date_trunc('week', u.createdAt) = date_trunc('week', CURRENT_DATE)
    """
    )
    fun countWeeklyLog(userId: UUID, type: UserActivityType, language: LanguageName): Int

    @Query(
        """
        SELECT COUNT(u) FROM UserActivityLog u
        WHERE 
            u.user.id = :userId
            AND u.type = :type
            AND u.language = :language
            AND date_trunc('week', u.createdAt) = date_trunc('week', CURRENT_DATE)
    """
    )
    fun countMonthlyLog(userId: UUID, type: UserActivityType, language: LanguageName): Int
}