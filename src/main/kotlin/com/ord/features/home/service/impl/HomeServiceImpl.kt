package com.ord.features.home.service.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
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
) : HomeService {
    override fun getHome(
        userId: UUID,
        language: LanguageName?,
        window: HomeWindow,
    ): Mono<HomeResponse> {
        if (language == null) {
            return Mono.just(HomeAggregator.empty(window))
        }

        return homeSummaryRepository
            .load(
                userId = userId,
                language = language,
                window = window,
            )
            .map { snapshot ->
                HomeAggregator.assemble(
                    window = window,
                    snapshot = snapshot,
                )
            }
    }
}
