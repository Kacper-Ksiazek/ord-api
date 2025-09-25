package com.ord.shared.repositories

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

/**
 * Common interface for repositories that handle user-scoped resources.
 * Provides standard user-scoped operations that all user resources should support.
 */
interface UserResourceRepository<TEntity : Any> {
    /**
     * Find all entities for a specific user
     */
    fun findAllForUser(userId: UUID): Flux<TEntity>

    /**
     * Find specific entities by their IDs for a specific user
     */
    fun findAllForUser(ids: Set<UUID>, userId: UUID): Flux<TEntity>

    /**
     * Find a single entity by ID for a specific user
     */
    fun findOneForUser(userId: UUID, id: UUID): Mono<TEntity?>

    /**
     * Delete a single entity by ID for a specific user
     * @return Number of deleted records (0 or 1)
     */
    fun deleteOneForUser(userId: UUID, id: UUID): Mono<Int>
}