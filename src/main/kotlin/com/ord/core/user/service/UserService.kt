package com.ord.core.user.service

import com.ord.core.user.model.UserEntity
import java.util.*

interface UserService {
    fun findAll(): List<UserEntity>

    fun findById(id: UUID): UserEntity?

    fun save(user: UserEntity): UserEntity

    fun findUserByAuthToken(authToken: String): UserEntity?

    fun findUserByEmail(email: String): UserEntity?
}