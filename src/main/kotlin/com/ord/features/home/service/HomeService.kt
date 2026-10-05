package com.ord.features.home.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.home.model.HomeActivityPerDay
import com.ord.features.home.model.HomeResponse
import com.ord.features.home.model.HomeWindow
import reactor.core.publisher.Mono
import java.util.UUID

interface HomeService {
    fun getHome(
        userId: UUID,
        language: LanguageName?,
        window: HomeWindow,
    ): Mono<HomeResponse>

    fun getActivity(
        userId: UUID,
        language: LanguageName?,
        window: HomeWindow,
    ): Mono<HomeActivityPerDay>
}
