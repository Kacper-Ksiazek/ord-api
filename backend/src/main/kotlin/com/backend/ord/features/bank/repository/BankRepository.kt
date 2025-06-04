package com.backend.ord.features.bank.repository

import com.backend.ord.features.bank.model.BankEntity
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BankRepository : UserResourceRepository<BankEntity> {
    @Query("SELECT b FROM BankEntity b WHERE b.user.id = :userId AND lower(b.name) LIKE lower(concat('%', :phrase, '%'))")
    fun findAllForUserBySearchingPhrase(userId: UUID, phrase: String): List<BankEntity>
}