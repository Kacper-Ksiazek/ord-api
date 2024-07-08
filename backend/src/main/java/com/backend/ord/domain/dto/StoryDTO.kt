package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase

data class StoryDTO(
    var title: String,
    var content: String,
    var explanations: MutableMap<String, String> = mutableMapOf(),

    val user: UserDTO
) : DTOBase()
