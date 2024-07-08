package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase

data class BankGroupDTO(
    var name: String,
    var color: String
) : DTOBase()
