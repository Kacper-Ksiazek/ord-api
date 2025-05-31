package com.backend.ord.core.word.api.facades

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.requests.dto.ChangeBankForMultipleWordsRequest
import com.backend.ord.core.word.api.requests.dto.ChangeBankForSingleWordRequest
import java.util.*

interface WordBankManagementFacade {
    fun changeBankOfOneWord(id: UUID, body: ChangeBankForSingleWordRequest, user: UserEntity)

    fun changeBankOfMultipleWords(body: ChangeBankForMultipleWordsRequest, user: UserEntity)
}