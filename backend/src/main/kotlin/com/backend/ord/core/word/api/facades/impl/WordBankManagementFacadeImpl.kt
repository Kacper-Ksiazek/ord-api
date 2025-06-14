package com.backend.ord.core.word.api.facades.impl

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.facades.WordBankManagementFacade
import com.backend.ord.core.word.api.facades.internal.getBankFromRequest
import com.backend.ord.core.word.api.facades.internal.getBankFromRequestOrNull
import com.backend.ord.core.word.api.requests.dto.ChangeBankForMultipleWordsRequest
import com.backend.ord.core.word.api.requests.dto.ChangeBankForSingleWordRequest
import com.backend.ord.core.word.service.WordService
import com.backend.ord.features.bank.service.BankService
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordBankManagementFacadeImpl(
    private val wordService: WordService,
    private val bankService: BankService
) : WordBankManagementFacade {
    override fun changeBankOfOneWord(
        id: UUID,
        body: ChangeBankForSingleWordRequest,
        user: UserEntity
    ) {
        val bank = getBankFromRequestOrNull(
            bankService = bankService,
            user = user,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate
        )

        wordService.changeBankForSingleWord(
            wordId = id,
            bankId = bank?.id,
            userId = user.id
        )
    }

    override fun changeBankOfMultipleWords(
        body: ChangeBankForMultipleWordsRequest,
        user: UserEntity
    ) {
        val bank = getBankFromRequest(
            bankService = bankService,
            user = user,
            bankId = body.bankId,
            bankToCreate = body.bankToCreate
        )

        wordService.changeBankForMultipleWords(
            wordIds = body.wordIds,
            bankId = bank.id,
            userId = user.id
        )

    }
}