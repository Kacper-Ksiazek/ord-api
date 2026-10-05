package com.ord.features.home.model

import com.ord.core.word.api.crud.responses.dto.WordListItem
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.conversation.models.conversation.ConversationSummaryDTO
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Landing summary for the authenticated user and their selected learning language")
data class HomeResponse(
    val words: HomeWordsSection,
    val conversations: HomeConversationsSection,
    val games: HomeGamesSection,
    val trends: HomeTrendsSection,
    val activity: HomeActivitySection,

    @Schema(description = "Up to three most recently created words in the selected learning language")
    val recentWords: List<WordListItem>,

    @Schema(description = "Up to three most recently updated conversations in the selected learning language")
    val recentConversations: List<ConversationSummaryDTO>,
)

@Schema(description = "Vocabulary counts for the selected learning language")
data class HomeWordsSection(
    @Schema(description = "All words in the selected language", example = "40")
    val total: Long,

    @Schema(description = "Words created in the current UTC calendar month, including today", example = "6")
    val addedLast30Days: Long,

    @Schema(description = "Count per word type. Types with zero words are omitted.")
    val byType: Map<WordType, Long>,
)

@Schema(description = "Conversation counts for the selected learning language")
data class HomeConversationsSection(
    @Schema(description = "All conversations in the selected language", example = "3")
    val total: Long,

    @Schema(description = "All messages in those conversations, from the user and the AI", example = "28")
    val messagesTotal: Long,

    @Schema(description = "Conversations created in the current UTC calendar month, including today", example = "1")
    val createdLast30Days: Long,

    @Schema(description = "Messages created in the current UTC calendar month, from the user and the AI", example = "8")
    val messagesLast30Days: Long,
)

@Schema(description = "Finished-game counts. comingSoon stays true until the games UI ships.")
data class HomeGamesSection(
    @Schema(description = "Games practice is not on the home screen yet", example = "true")
    val comingSoon: Boolean,

    @Schema(description = "Finished games stored for this user and language. Null when that count is not available.", nullable = true)
    val total: Long?,

    @Schema(description = "Finished games recorded in the current UTC calendar month. Null when that count is not available.", nullable = true)
    val last30Days: Long?,
)

@Schema(description = "Days in the current UTC calendar year with any learning activity")
data class HomeActivitySection(
    @Schema(description = "Current calendar year in UTC", example = "2026")
    val year: Int,

    @Schema(description = "Days in that year whose count is greater than zero")
    val days: List<HomeActivityDay>,
)

@Schema(description = "One UTC day of home activity")
data class HomeActivityDay(
    @Schema(description = "UTC date", example = "2026-09-30")
    val date: String,

    @Schema(description = "Words added, conversation messages, and finished games recorded that day", example = "4")
    val count: Long,
)
