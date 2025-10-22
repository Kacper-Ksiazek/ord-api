package com.ord.core.word.api.crud.facades

import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.crud.requests.dto.ChangeBankForMultipleWordsRequest
import com.ord.core.word.api.crud.requests.dto.ChangeBankForSingleWordRequest
import reactor.core.publisher.Mono
import java.util.*

interface WordBankManagementFacade {
    fun changeBankOfOneWord(
        id: UUID,
        body: ChangeBankForSingleWordRequest,
        user: UserDTO
    ): Mono<Void>

    fun changeBankOfMultipleWords(
        body: ChangeBankForMultipleWordsRequest,
        user: UserDTO
    ): Mono<Void>
}