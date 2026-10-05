package com.ord.features.home.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.home.model.HomeActivityPerDay
import com.ord.features.home.model.HomeAggregator
import com.ord.features.home.model.HomeResponse
import com.ord.features.home.model.HomeWindow
import com.ord.features.home.model.parts.HomeRecentContent
import com.ord.features.home.repository.HomeRepository
import com.ord.features.home.service.HomeService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class HomeServiceImpl(
    private val homeRepository: HomeRepository,
) : HomeService {
    override fun getHome(
        userId: UUID,
        language: LanguageName?,
        window: HomeWindow,
    ): Mono<HomeResponse> {
        if (language == null) {
            return Mono.just(HomeAggregator.empty(window))
        }

        return homeRepository
            .loadOverviews(
                userId = userId,
                language = language,
                window = window,
            )
            .flatMap { snapshot ->
                homeRepository
                    .loadRecentWords(
                        userId = userId,
                        language = language,
                        limit = RECENT_LIMIT,
                    )
                    .collectList()
                    .flatMap { words ->
                        homeRepository
                            .loadRecentConversations(
                                userId = userId,
                                language = language,
                                limit = RECENT_LIMIT,
                            )
                            .collectList()
                            .map { conversations ->
                                HomeAggregator.assemble(
                                    window = window,
                                    snapshot = snapshot,
                                ).copy(
                                    recentContent = HomeRecentContent(
                                        words = words,
                                        conversations = conversations,
                                    ),
                                )
                            }
                    }
            }
    }

    override fun getActivity(
        userId: UUID,
        language: LanguageName?,
        window: HomeWindow,
    ): Mono<HomeActivityPerDay> {
        if (language == null) {
            return Mono.just(HomeAggregator.activity(window, emptyList()))
        }

        return homeRepository
            .loadActivityDays(
                userId = userId,
                language = language,
                window = window,
            )
            .collectList()
            .map { days -> HomeAggregator.activity(window, days) }
    }

    private companion object {
        const val RECENT_LIMIT: Int = 3
    }
}
