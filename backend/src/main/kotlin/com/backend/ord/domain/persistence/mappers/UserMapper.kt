package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.bases.MapperBase

interface UserMapper : MapperBase<User, UserDTO>
