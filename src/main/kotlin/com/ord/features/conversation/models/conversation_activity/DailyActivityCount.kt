package com.ord.features.conversation.models.conversation_activity

import java.time.LocalDate

data class DailyActivityCount(
    val date: LocalDate,
    val count: Long,
)
