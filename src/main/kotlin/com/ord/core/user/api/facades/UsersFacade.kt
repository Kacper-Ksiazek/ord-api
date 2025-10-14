package com.ord.core.user.api.facades

import com.ord.core.user.api.requests.InitUserAccountRequest
import com.ord.core.user.api.responses.MeResponse
import com.ord.core.user.model.UserDTO
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import java.util.UUID

interface UsersFacade {
    fun me(
        user: UserDTO,
    ): Mono<ResponseEntity<MeResponse>>


    fun initUserAccount(
        userId: UUID,
        body: InitUserAccountRequest,
    ): Mono<ResponseEntity<MeResponse>>
}
