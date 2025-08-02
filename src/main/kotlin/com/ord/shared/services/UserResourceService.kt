package com.ord.shared.services

import com.ord.exceptions.REST.NotFoundException
import com.ord.shared.models.IdentifiableUserResource
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserResourceService<TEntity : IdentifiableUserResource> {
    val repository: UserResourceRepository<TEntity>

    fun save(t: TEntity): TEntity {
        return repository.save(t)
    }

    fun update(
        t: TEntity,
        userId: UUID,
    ): TEntity {
        // Verify that user is the owner of the entity
        this.findByIdOrFail(t.id, userId).let {
            if (it.user.id != userId) throw NotFoundException("Entity not found")
        }

        return repository.save(t)
    }

    @Transactional
    fun deleteById(
        id: UUID,
        userId: UUID,
    ) {
        repository.deleteOneForUser(userId, id).let {
            if (it == 0) throw NotFoundException("Entity with id $id for user with id $userId not found")
        }
    }

    fun findById(
        id: UUID,
        userId: UUID? = null
    ): TEntity? {
        if (userId == null) {
            return repository.findByIdOrNull(id)
        }

        return repository.findOneForUser(userId, id)
    }

    fun findByIdOrFail(
        id: UUID,
        userId: UUID? = null,
        message: String = "Entity not found"
    ): TEntity {
        return this.findById(id, userId) ?: throw NotFoundException(message)
    }

    fun findAll(userId: UUID? = null): List<TEntity> {
        if (userId == null) {
            return repository.findAll()
        }

        return repository.findAllForUser(userId)
    }
}