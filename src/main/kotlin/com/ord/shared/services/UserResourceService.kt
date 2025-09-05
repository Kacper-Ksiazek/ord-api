package com.ord.shared.services

import com.ord.exceptions.REST.NotFoundException
import com.ord.shared.models.IdentifiableUserResource
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
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
        return findByIdOrFail(t.id, userId)
            .flatMap { existing ->
                if (existing.user?.id != userId) {
                    Mono.error<TEntity>(NotFoundException("Entity not found"))
                } else {
                    repository.save(t)
                }
            }
    }


    @Transactional
    fun deleteById(
        id: UUID,
        userId: UUID,
    ): Mono<Void> {
        return repository.deleteOneForUser(userId, id)
            .flatMap { deletedCount ->
                if (deletedCount == 0) Mono.error(NotFoundException("Entity with id $id for user with id $userId not found"))
                else Mono.empty()
            }
    }


    fun findById(
        id: UUID,
        userId: UUID? = null
    ): Mono<TEntity> {
        return if (userId == null) {
            repository.findById(id)
        } else {
            repository.findOneForUser(userId, id)
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
            repository.findAllForUser(userId)
        }
    }
}

