package com.ord.shared.services

import com.ord.exceptions.REST.InternalServerError
import com.ord.exceptions.REST.NotFoundException
import com.ord.shared.models.IdentifiableUserResource
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface UserResourceService<TEntity : IdentifiableUserResource> {
    val repository: UserResourceRepository<TEntity>

    fun save(t: TEntity): Mono<TEntity> {
        return repository.save(t)
    }


    fun update(
        t: TEntity,
        userId: UUID,
    ): Mono<TEntity> {
        return findByIdOrFail(t.id!!, userId)
            .flatMap { existing ->
                if (existing.userId != userId) {
                    Mono.error<TEntity>(NotFoundException("Entity not found"))
                } else {
                    repository.save(t)
                }
            }
    }


    fun deleteById(
        id: UUID,
        userId: UUID,
    ): Mono<Void> {
        return findByIdOrFail(id = id, userId = userId)
            .flatMap { existing ->
                repository.deleteByIdAndUserId(id = id, userId = userId)
            }
    }


    fun findById(
        id: UUID,
        userId: UUID? = null
    ): Mono<TEntity> {
        return if (userId == null) {
            repository.findById(id)
        } else {
            repository.findByIdAndUserId(id = id, userId = userId)
        }
    }


    fun findByIdOrFail(
        id: UUID,
        userId: UUID? = null,
        message: String = "Entity not found"
    ): Mono<TEntity> {
        return findById(id, userId)
            .switchIfEmpty(Mono.error(NotFoundException(message)))
    }


    fun findAll(
        userId: UUID? = null
    ): Flux<TEntity> {
        return if (userId == null) {
            repository.findAll()
        } else {
            repository.findAllByUserId(userId)
        }
    }
}

