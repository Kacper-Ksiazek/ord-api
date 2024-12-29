package com.backend.ord.domain.persistance.mappers

import com.backend.ord.domain.persistance.dto.UserDTO
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface UserMapper : MapperBase<User, UserDTO>
