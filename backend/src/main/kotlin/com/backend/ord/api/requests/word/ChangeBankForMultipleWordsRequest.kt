package com.backend.ord.api.requests.word

import java.util.*

interface ChangeBankForMultipleWordsRequest {
    val wordIds: List<UUID>
    val bankId: UUID?
}