package com.backend.ord.services.bases

import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

interface UserResourceService<T : IdentifiableUserResource> {
    val repository: UserResourceRepository<T>

    fun save(t: T): T {
        return repository.save(t!!)
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

    fun findAll(userId: UUID? = null): List<T> {
        if (userId == null) {
            return repository.findAll()
        }

        return repository.findAllForUser(userId)
    }
}