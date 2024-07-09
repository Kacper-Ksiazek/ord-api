package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel

data class LanguageProficiencyDTO(
    var language: LanguageName,
    var proficiency: LanguageProficiencyLevel,

    var user: UserDTO
) : DTOBase()
