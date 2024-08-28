package com.backend.ord.api.requests.bank.data

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateBankRequestData(
    @field:NotBlank(message = "Bank name cannot be blank")
    @field:Size(min = 2, max = 165, message = "Bank name must be between 1 and 64 characters")
    override val name: String,

    @field:NotBlank(message = "Bank description cannot be blank")
    @field:Size(min = 1, max = 255, message = "Bank description must be between 1 and 255 characters")
    override val description: String,

    override val groupId: UUID? = null
) : CreateBankRequest