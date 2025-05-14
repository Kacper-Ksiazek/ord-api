package com.backend.ord.domain.persistence.mappers

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.shared.models.MapperBase

interface userMapper : MapperBase<UserEntity, UserDTO>
