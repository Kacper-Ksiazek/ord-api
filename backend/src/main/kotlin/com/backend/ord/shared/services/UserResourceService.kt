package com.backend.ord.shared.services

import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.shared.models.IdentifiableUserResource
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserResourceService<T : IdentifiableUserResource> {
    val repository: UserResourceRepository<T>

    fun save(t: T): T {
        return repository.save(t)
    }

    fun update(
        t: T,
        userId: UUID,
    ): T {
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
    ): T? {
        if (userId == null) {
            return repository.findByIdOrNull(id)
        }

        return repository.findOneForUser(userId, id)
    }

    fun findByIdOrFail(
        id: UUID,
        userId: UUID? = null,
        message: String = "Entity not found"
    ): T {
        return this.findById(id, userId) ?: throw NotFoundException(message)
    }

    fun findAll(userId: UUID? = null): List<T> {
        if (userId == null) {
            return repository.findAll()
        }

        return repository.findAllForUser(userId)
    }
}