package com.backend.ord.repositories

import com.backend.ord.domain.persistence.entities.BankGroup
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BankGroupRepository : UserResourceRepository<BankGroup> {
    @Query("SELECT bg FROM BankGroup bg WHERE bg.user.id = :userId AND lower(bg.name) LIKE lower(concat('%', :phrase, '%'))")
    fun findAllForUserBySearchingPhrase(userId: UUID, phrase: String): List<BankGroup>
}