package com.backend.ord.services.impl

import com.backend.ord.domain.entities.Bank
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.BankService
import org.springframework.stereotype.Service

@Service
class BankServiceImpl(
    override val repository: UserResourceRepository<Bank>
) : BankService