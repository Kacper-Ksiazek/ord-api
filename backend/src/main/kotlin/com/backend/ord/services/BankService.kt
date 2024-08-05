package com.backend.ord.services

import com.backend.ord.domain.entities.Bank
import com.backend.ord.services.bases.UserResourceService
import org.springframework.stereotype.Service

@Service
interface BankService : UserResourceService<Bank>