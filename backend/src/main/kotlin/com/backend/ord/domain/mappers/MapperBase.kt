package com.backend.ord.domain.mappers

interface MapperBase<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toEntity(dto: DTO): Entity

    fun toDTO(entity: Entity?): DTO? {
        return entity?.let { toDTO(it) }
    }

    fun toEntity(dto: DTO?): Entity? {
        return dto?.let { toEntity(it) }
    }
}
