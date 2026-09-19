package com.ord.core.security

import com.ord.core.auth.models.UserSessionEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserSessionRepositoryReactive : ReactiveCrudRepository<UserSessionEntity, UUID> {
    fun findByTokenHash(tokenHash: String): Mono<UserSessionEntity>

    fun deleteByTokenHash(tokenHash: String): Mono<Void>
}
