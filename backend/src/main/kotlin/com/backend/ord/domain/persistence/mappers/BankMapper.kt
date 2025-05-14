package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.BankDTO
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.shared.models.mappers.MapperBase

interface BankMapper : MapperBase<Bank, BankDTO>