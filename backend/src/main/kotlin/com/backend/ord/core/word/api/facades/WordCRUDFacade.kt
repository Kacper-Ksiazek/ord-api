package com.backend.ord.core.word.api.facades

import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.requests.dto.CreateWordRequest
import com.backend.ord.core.word.api.requests.dto.GetManyWordsRequest
import com.backend.ord.core.word.api.requests.dto.UpdateWordRequest
import com.backend.ord.core.word.api.responses.dto.SingleWordResponse
import com.backend.ord.core.word.api.responses.dto.WordListItem
import com.backend.ord.core.word.model.WordDTO
import java.util.*

interface WordCRUDFacade {
    fun getManyWords(requestBody: GetManyWordsRequest, user: UserEntity): PaginatedDataResponse<WordListItem>

    fun getSingleWord(id: UUID, user: UserEntity): SingleWordResponse

    fun createWord(body: CreateWordRequest, user: UserEntity): WordDTO

    fun updateWord(id: UUID, body: UpdateWordRequest, user: UserEntity): WordDTO

    fun deleteWord(id: UUID, user: UserEntity)
}