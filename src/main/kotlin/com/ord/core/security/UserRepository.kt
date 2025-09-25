package com.ord.core.security

import com.ord.core.user.model.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserRepository : ReactiveCrudRepository<UserEntity, UUID> {
    fun findByEmail(email: String): Mono<UserEntity?>

    fun deleteByEmail(email: String): Mono<Void>
}