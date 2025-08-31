package com.ord.core.security

import com.ord.core.auth.models.UserSessionEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserSessionRepositoryReactive : ReactiveCrudRepository<UserSessionEntity, UUID> {
    fun findByToken(token: String): Mono<UserSessionEntity?>

    fun deleteByToken(token: String): Mono<Void>
}
