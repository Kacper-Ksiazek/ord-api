package com.backend.ord.repositories

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LanguageProficiencyRepository : UserResourceRepository<LanguageProficiency> {
    @Query(
        """ 
        SELECT * FROM language_proficiencies 
        WHERE 
            user_id = :userId 
            AND 
            CAST(language as text) = CAST(:languageName AS text)
    """,
        nativeQuery = true
    )
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: LanguageName
    ): LanguageProficiency?

    @Query(
        """
    SELECT
        *
    FROM language_proficiencies lp
    WHERE lp.user_id = :userId AND CAST(lp.language as text) = CAST(:languageName AS text)
    """,
        nativeQuery = true
    )
    fun testQuery(
        @Param("userId") userId: UUID,
        @Param("languageName") languageName: String
    ): List<Map<String, Any>>
}
