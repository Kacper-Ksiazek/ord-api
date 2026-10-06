package com.ord.features.home.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.home.model.HomeActivityPerDay
import com.ord.features.home.model.HomeResponse
import com.ord.features.home.model.HomeWindow
import com.ord.features.home.model.emptyHomeResponse
import com.ord.features.home.model.toActivity
import com.ord.features.home.model.toHomeResponse
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
            return Mono.just(window.emptyHomeResponse())
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
                                snapshot.toHomeResponse(window).copy(
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
            return Mono.just(window.toActivity(emptyList()))
        }

        return homeRepository
            .loadActivityDays(
                userId = userId,
                language = language,
                window = window,
            )
            .collectList()
            .map { days -> window.toActivity(days) }
    }

    private companion object {
        const val RECENT_LIMIT: Int = 3
    }
}
