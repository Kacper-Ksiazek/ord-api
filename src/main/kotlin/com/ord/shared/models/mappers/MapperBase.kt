package com.ord.shared.models.mappers

interface MapperBase<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toEntity(dto: DTO): Entity

    fun toDTOOrNull(entity: Entity?): DTO? {
        return entity?.let { toDTO(it) }
    }

    fun toEntityOrNull(dto: DTO?): Entity? {
        return dto?.let { toEntity(it) }
    }

    fun toDTOList(entities: List<Entity>): List<DTO> {
        return entities.map { toDTO(it) }
    }

    fun toEntityList(dtos: List<DTO>): List<Entity> {
        return dtos.map { toEntity(it) }
    }
}