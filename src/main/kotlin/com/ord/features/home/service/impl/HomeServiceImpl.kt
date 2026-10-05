package com.ord.features.home.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.repositories.WordRepository
import com.ord.features.conversation.models.conversation.ConversationSummaryMapper
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.features.home.model.HomeAggregator
import com.ord.features.home.model.HomeResponse
import com.ord.features.home.model.HomeWindow
import com.ord.features.home.repository.HomeSummaryRepository
import com.ord.features.home.service.HomeService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class HomeServiceImpl(
    private val homeSummaryRepository: HomeSummaryRepository,
    private val wordRepository: WordRepository,
    private val conversationRepository: ConversationRepository,
    private val conversationSummaryMapper: ConversationSummaryMapper,
) : HomeService {
    override fun getHome(
        userId: UUID,
        language: LanguageName?,
        window: HomeWindow,
    ): Mono<HomeResponse> {
        if (language == null) {
            return Mono.just(HomeAggregator.empty(window))
        }

        return Mono.zip(
            homeSummaryRepository.load(
                userId = userId,
                language = language,
                window = window,
            ),
            wordRepository
                .findLatestListItems(
                    userId = userId,
                    language = language,
                    limit = RECENT_LIMIT,
                )
                .collectList(),
            conversationRepository
                .findLatest(
                    userId = userId,
                    language = language,
                    limit = RECENT_LIMIT,
                )
                .collectList(),
        ).map { tuple ->
            HomeAggregator.assemble(
                window = window,
                snapshot = tuple.t1,
            ).copy(
                recentWords = tuple.t2,
                recentConversations = tuple.t3.map(conversationSummaryMapper::toDTO),
            )
        }
    }

    private companion object {
        const val RECENT_LIMIT: Int = 3
    }
}
