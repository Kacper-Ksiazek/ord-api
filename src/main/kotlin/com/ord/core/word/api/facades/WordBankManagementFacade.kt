package com.ord.core.word.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.dto.ChangeBankForMultipleWordsRequest
import com.ord.core.word.api.requests.dto.ChangeBankForSingleWordRequest
import java.util.*

interface WordBankManagementFacade {
    fun changeBankOfOneWord(id: UUID, body: ChangeBankForSingleWordRequest, user: UserEntity)

    fun changeBankOfMultipleWords(body: ChangeBankForMultipleWordsRequest, user: UserEntity)
}