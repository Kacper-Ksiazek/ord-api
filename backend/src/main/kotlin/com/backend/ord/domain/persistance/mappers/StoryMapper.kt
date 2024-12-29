package com.backend.ord.domain.persistance.mappers

import com.backend.ord.domain.persistance.dto.StoryDTO
import com.backend.ord.domain.persistance.entities.Story
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface StoryMapper : MapperBase<Story, StoryDTO>