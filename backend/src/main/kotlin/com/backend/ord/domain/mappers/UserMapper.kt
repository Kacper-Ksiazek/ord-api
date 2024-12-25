package com.backend.ord.domain.mappers

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.bases.MapperBase

interface UserMapper : MapperBase<User, UserDTO>
