package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.word.ChangeBankForMultipleWordsRequest
import java.util.*

data class ChangeBankForMultipleWordsRequestData(
    override val wordIds: List<UUID>,
    override val bankId: UUID?
) : ChangeBankForMultipleWordsRequest