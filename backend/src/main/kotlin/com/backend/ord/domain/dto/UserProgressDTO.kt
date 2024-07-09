package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase

class UserProgressDTO(
    var pointsObtained: Int = 0,

    var user: UserDTO,
    var game: GameDTO
) : DTOBase()
