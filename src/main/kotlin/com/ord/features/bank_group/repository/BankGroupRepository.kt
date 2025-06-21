package com.ord.features.bank_group.repository

import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BankGroupRepository : UserResourceRepository<BankGroupEntity> {
    @Query("SELECT bg FROM BankGroupEntity bg WHERE bg.user.id = :userId AND lower(bg.name) LIKE lower(concat('%', :phrase, '%'))")
    fun findAllForUserBySearchingPhrase(userId: UUID, phrase: String): List<BankGroupEntity>
}