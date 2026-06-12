package com.ord.features.quickly_added_words.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.features.quickly_added_words.api.requests.QAWFillGapsRequest
import com.ord.features.quickly_added_words.api.responses.QAWFillGapsResponse
import reactor.core.publisher.Mono

interface QAWAIFacade {
    fun fillGaps(
        body: QAWFillGapsRequest,
        user: UserDTO,
    ): Mono<QAWFillGapsResponse>
}
