package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase

data class BankDTO(
    var name: String,
    var description: String,

    val user: UserDTO,
    var group: BankGroupDTO
) : DTOBase()
