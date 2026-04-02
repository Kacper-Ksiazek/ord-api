package com.ord.features.conversation.api.facades.impl

import com.ord.features.conversation.api.facades.ConversationActivityFacade
import com.ord.features.conversation.models.conversation_activity.ConversationActivityCalculator
import com.ord.features.conversation.models.conversation_activity.ConversationActivityOverviewDTO
import com.ord.features.conversation.services.ConversationMessageService
import com.ord.features.conversation.services.ConversationService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Service
class ConversationActivityFacadeImpl(
    private val conversationService: ConversationService,
    private val conversationMessageService: ConversationMessageService,
) : ConversationActivityFacade {

    override fun getActivityOverview(userId: UUID): Mono<ResponseEntity<ConversationActivityOverviewDTO>> {
        val now = LocalDate.now(ZoneOffset.UTC)
        val startDate = now.minusDays(89)
        val from = startDate.atStartOfDay(ZoneOffset.UTC).toInstant()
        val to = now.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()

        val dailyConversations = conversationService
            .countDailyNewConversations(userId, from, to)
            .collectList()

        val dailyMessages = conversationMessageService
            .countDailyMessages(userId, from, to)
            .collectList()

        return Mono.zip(dailyConversations, dailyMessages)
            .map { tuple ->
                val convMap = tuple.t1.associate { it.date to it.count }
                val msgMap = tuple.t2.associate { it.date to it.count }

                val heatmap = ConversationActivityCalculator.buildHeatmap(msgMap, startDate, now)
                val messagesByWeek = ConversationActivityCalculator.buildWeeklySeries(msgMap, startDate, now)
                val conversationsByWeek = ConversationActivityCalculator.buildWeeklySeries(convMap, startDate, now)

                val overview = ConversationActivityOverviewDTO(
                    periodLabel = "Last 90 days",
                    messagesTotal = heatmap.sumOf { it.count },
                    conversationsTotal = convMap.values.sum(),
                    heatmap = heatmap,
                    messagesByWeek = messagesByWeek,
                    conversationsByWeek = conversationsByWeek,
                )

                ResponseEntity.ok(overview)
            }
    }
}
