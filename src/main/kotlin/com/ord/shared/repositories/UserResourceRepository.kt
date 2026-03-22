package com.ord.shared.repositories

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@NoRepositoryBean
interface UserResourceRepository<TEntity : Any> : ReactiveCrudRepository<TEntity, UUID> {
    fun findAllByUserId(userId: UUID): Flux<TEntity>


    fun findAllByIdInAndUserId(ids: Set<UUID>, userId: UUID): Flux<TEntity>


    fun findByIdAndUserId(id: UUID, userId: UUID): Mono<TEntity?>


    fun deleteByIdAndUserId(id: UUID, userId: UUID): Mono<Void>
}