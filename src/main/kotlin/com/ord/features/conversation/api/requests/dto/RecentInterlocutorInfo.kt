package com.ord.features.conversation.api.requests.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RecentInterlocutorInfo(
    @field:NotBlank(message = "Avatar ID cannot be blank")
    val avatarId: String,

    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(max = 200, message = "Name must be at most 200 characters")
    val name: String
)
