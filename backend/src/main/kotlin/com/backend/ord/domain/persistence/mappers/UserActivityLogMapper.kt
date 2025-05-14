package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.UserActivityLogDTO
import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.shared.models.MapperBase

interface UserActivityLogMapper : MapperBase<UserActivityLog, UserActivityLogDTO>