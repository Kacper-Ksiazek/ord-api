package com.ord.features.home.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.features.home.model.HomeResponse
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface HomeFacade {
    fun getHome(user: UserDTO): Mono<ResponseEntity<HomeResponse>>
}
