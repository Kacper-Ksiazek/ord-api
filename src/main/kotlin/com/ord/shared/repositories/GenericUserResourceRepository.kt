package com.ord.shared.repositories

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

abstract class GenericUserResourceRepository<TEntity : Any>(
    protected val template: R2dbcEntityTemplate,
    private val entityClass: Class<TEntity>
)  {
    fun findAllForUser(userId: UUID): Flux<TEntity> =
        template.select(
            Query.query(
                Criteria.where("user_id").`is`(userId)
            ),
            entityClass
        )


    fun findAllForUser(ids: Set<UUID>, userId: UUID): Flux<TEntity> =
        template.select(
            Query.query(
                Criteria.where("user_id").`is`(userId)
                    .and("id").`in`(ids)
            ),
            entityClass
        )


    fun findOneForUser(userId: UUID, id: UUID): Mono<TEntity?> =
        template.selectOne(
            Query.query(
                Criteria.where("user_id").`is`(userId)
                    .and("id").`is`(id)
            ),
            entityClass
        )


    fun deleteOneForUser(userId: UUID, id: UUID): Mono<Int> =
        template.delete(
            Query.query(
                Criteria.where("user_id").`is`(userId)
                    .and("id").`is`(id)
            ),
            entityClass
        ).map { it.toInt() }
}
