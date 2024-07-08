package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.enums.Language.LanguageName

data class QuicklyAddedWordDTO(
    var typedWord: String,
    var typedInLanguage: LanguageName,

    var user: UserDTO
) : DTOBase()
