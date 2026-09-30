package com.ord.features.home.api.facades.impl

import com.ord.core.user.model.UserDTO
import com.ord.features.home.api.facades.HomeFacade
import com.ord.features.home.model.HomeResponse
import com.ord.features.home.model.HomeWindow
import com.ord.features.home.service.HomeService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class HomeFacadeImpl(
    private val homeService: HomeService,
) : HomeFacade {
    override fun getHome(user: UserDTO): Mono<ResponseEntity<HomeResponse>> {
        val window = HomeWindow.at(LocalDate.now(ZoneOffset.UTC))

        return homeService
            .getHome(
                userId = user.id,
                language = user.selectedLearningLanguage,
                window = window,
            )
            .map { ResponseEntity.ok(it) }
    }
}
