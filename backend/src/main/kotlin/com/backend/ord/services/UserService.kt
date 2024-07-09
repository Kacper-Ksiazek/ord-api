package com.backend.ord.services

import com.backend.ord.domain.entities.User
import java.util.*

interface UserService {
    fun findAll(): List<User>

    fun findById(id: UUID): User?

    fun save(user: User): User?

    fun findUserByAuthToken(authToken: String): User?

    fun findUserByEmail(email: String): User?
}
