package com.backend.ord.domain.mappers

interface MapperBase<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toEntity(dto: DTO): Entity

    fun toDTOOrNull(entity: Entity?): DTO? {
        return entity?.let { toDTO(it) }
    }

    fun toEntityOrNull(dto: DTO?): Entity? {
        return dto?.let { toEntity(it) }
    }
}
