package com.ord.features.bank.repository

import com.ord.features.bank.model.BankEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface BankRepository : UserResourceRepository<BankEntity>