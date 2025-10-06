package com.ord.core.langugae_proficiency

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

interface LanguageProficiencyRepository : UserResourceRepository<LanguageProficiencyEntity> {

    @Query(
        """
        SELECT * FROM language_proficiencies 
        WHERE user_id = :userId 
        AND language = :languageName
        """
    )
    fun findUserProficiencyInLanguage(
        userId: UUID,
        languageName: String
    ): Mono<LanguageProficiencyEntity?>
}

