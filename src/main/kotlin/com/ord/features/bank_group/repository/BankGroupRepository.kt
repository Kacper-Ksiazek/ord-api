package com.ord.features.bank_group.repository

import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BankGroupRepository :
    UserResourceRepository<BankGroupEntity>,
    ReactiveCrudRepository<BankGroupEntity, UUID>