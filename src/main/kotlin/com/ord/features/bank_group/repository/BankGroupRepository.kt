package com.ord.features.bank_group.repository

import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface BankGroupRepository : UserResourceRepository<BankGroupEntity>