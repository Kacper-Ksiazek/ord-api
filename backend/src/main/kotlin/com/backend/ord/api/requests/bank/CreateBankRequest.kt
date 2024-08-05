package com.backend.ord.api.requests.bank

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateBankRequest(
    @field:NotBlank(message = "Bank name cannot be blank")
    @field:Size(min = 1, max = 64, message = "Bank name must be between 1 and 64 characters")
    var name: String,

    @field:NotBlank(message = "Bank description cannot be blank")
    @field:Size(min = 1, max = 255, message = "Bank description must be between 1 and 255 characters")
    var description: String,

    var groupId: UUID? = null
)