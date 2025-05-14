package com.backend.ord.repositories

import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LanguageProficiencyRepository : UserResourceRepository<LanguageProficiencyEntity> {
    @Query(
        """ 
        SELECT * FROM language_proficiencies 
        WHERE 
            user_id = :userId 
            AND 
            language = :languageName 
        """,
        nativeQuery = true
    )
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: String
    ): LanguageProficiencyEntity?

    @Query(
        """
        SELECT * FROM language_proficiencies lp
        WHERE 
            lp.user_id = :userId 
            AND 
            :languageName = lp.language
        """,
        nativeQuery = true
    )
    fun testQuery(
        @Param("userId") userId: UUID,
        @Param("languageName") languageName: String
    ): List<Map<String, Any>>
}
