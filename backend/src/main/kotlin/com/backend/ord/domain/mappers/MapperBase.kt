package com.backend.ord.domain.mappers

interface MapperBase<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toEntity(dto: DTO): Entity
}
