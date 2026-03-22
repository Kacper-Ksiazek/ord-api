package com.ord.features.conversation.models.conversation

import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation.enums.RecencyBucket

data class ConversationListFilters(
    val search: String? = null,
    val recencyBucket: RecencyBucket? = null,
    val type: ConversationType? = null
)
