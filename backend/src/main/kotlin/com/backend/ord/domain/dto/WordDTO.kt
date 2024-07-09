package com.backend.ord.domain.dto

import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordType

class WordDTO(
    var origin: String,
    var translation: String,
    var isBookmarked: Boolean = false,
    var type: WordType,
    var translatedFrom: LanguageName,
    var translatedTo: LanguageName,

    var points: Int = 0,
    var exampleSentences: MutableSet<ExampleSentence> = mutableSetOf(),

    val user: UserDTO,
    var bank: BankDTO? = null
) : DTOBase()

