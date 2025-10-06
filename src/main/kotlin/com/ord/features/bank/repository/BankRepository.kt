package com.ord.features.bank.repository

import com.ord.features.bank.model.BankEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

interface BankRepository :
    UserResourceRepository<BankEntity>,
    ReactiveCrudRepository<BankEntity, UUID>