package com.ord.core.word.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.dto.ChangeBankForMultipleWordsRequest
import com.ord.core.word.api.requests.dto.ChangeBankForSingleWordRequest
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