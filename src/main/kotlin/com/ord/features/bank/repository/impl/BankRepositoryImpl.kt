package com.ord.features.bank.repository.impl

import com.ord.features.bank.model.BankEntity
import com.ord.shared.repositories.GenericUserResourceRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class BankRepositoryImpl(
    template: R2dbcEntityTemplate
) : GenericUserResourceRepository<BankEntity>(template) {
    override val entityClass = BankEntity::class.java
}