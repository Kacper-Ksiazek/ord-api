package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.StoryDTO
import com.backend.ord.domain.persistence.entities.Story
import com.backend.ord.shared.models.MapperBase

interface StoryMapper : MapperBase<Story, StoryDTO>