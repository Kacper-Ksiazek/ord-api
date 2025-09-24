package com.ord.core.word.api.facades.impl

import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.facades.WordBankManagementFacade
import com.ord.core.word.api.facades.internal.getBankFromRequest
import com.ord.core.word.api.facades.internal.getBankFromRequestOrNull
import com.ord.core.word.api.requests.dto.ChangeBankForMultipleWordsRequest
import com.ord.core.word.api.requests.dto.ChangeBankForSingleWordRequest
import com.ord.core.word.service.WordService
import com.ord.features.bank.service.BankService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordBankManagementFacadeImpl(
    private val wordService: WordService,
    private val bankService: BankService
) : WordBankManagementFacade {
    override fun changeBankOfOneWord(
        id: UUID,
        body: ChangeBankForSingleWordRequest,
        user: UserDTO
    ): Mono<Void> {
        return getBankFromRequestOrNull(
            bankService = bankService,
            userId = user.id,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate
        ).flatMap { bank ->
            wordService.changeBankForSingleWord(
                wordId = id,
                bankId = bank?.id,
                userId = user.id
            )
        }.then()
    }

    override fun changeBankOfMultipleWords(
        body: ChangeBankForMultipleWordsRequest,
        user: UserDTO
    ): Mono<Void> {
        return getBankFromRequest(
            bankService = bankService,
            userId = user.id,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate
        ).flatMap { bank ->
            wordService.changeBankForMultipleWords(
                wordIds = body.wordIds,
                bankId = bank.id,
                userId = user.id
            )
        }.then()
    }
}