package com.backend.ord.api.requests.word

import java.util.*

interface UpdateWordRequest : CreateWordRequest {
    /**
     * Word to update id
     */
    val id: UUID
}