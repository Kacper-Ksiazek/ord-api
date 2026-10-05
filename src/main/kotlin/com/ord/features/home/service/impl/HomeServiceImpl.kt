package com.ord.features.home.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.home.model.HomeAggregator
import com.ord.features.home.model.HomeResponse
import com.ord.features.home.model.HomeWindow
import com.ord.features.home.model.parts.HomeRecentContent
import com.ord.features.home.repository.HomeSummaryRepository
import com.ord.features.home.service.HomeService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class HomeServiceImpl(
    private val homeSummaryRepository: HomeSummaryRepository,
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
            homeSummaryRepository
                .loadRecentWords(
                    userId = userId,
                    language = language,
                    limit = RECENT_LIMIT,
                )
                .collectList(),
            homeSummaryRepository
                .loadRecentConversations(
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
                recentContent = HomeRecentContent(
                    words = tuple.t2,
                    conversations = tuple.t3,
                ),
            )
        }
    }

    private companion object {
        const val RECENT_LIMIT: Int = 3
    }
}
