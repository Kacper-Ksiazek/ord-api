package com.ord.shared.repositories

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@NoRepositoryBean
interface UserResourceRepository<TEntity : Any> : ReactiveCrudRepository<TEntity, UUID> {
    @Query(
        """
        SELECT * 
            FROM :#{#entityName} 
        WHERE user_id = :userId
        """
    )
    fun findAllForUser(userId: UUID): Flux<TEntity>


    @Query(
        """
        SELECT * 
            FROM :#{#entityName} 
        WHERE user_id = :userId 
            AND id = ANY(:ids)
        """
    )
    fun findAllForUser(ids: Set<UUID>, userId: UUID): Flux<TEntity>


    @Query(
        """
        SELECT * 
            FROM :#{#entityName} 
        WHERE user_id = :userId 
            AND id = :id
        """
    )
    fun findOneForUser(userId: UUID, id: UUID): Mono<TEntity>


    @Modifying
    @Query(
        """
        DELETE 
            FROM :#{#entityName} 
        WHERE user_id = :userId 
            AND id = :id
        """
    )
    fun deleteOneForUser(userId: UUID, id: UUID): Mono<Int>
}

