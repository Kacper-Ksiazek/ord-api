package com.ord.shared.models.mappers

interface UnidirectionalEntityMapper<Entity, DTO> {
    fun toDTO(entity: Entity): DTO

    fun toDTOOrNull(entity: Entity?): DTO? {
        return entity?.let { toDTO(it) }
    }

    fun toDTOList(entities: List<Entity>): List<DTO> {
        return entities.map { toDTO(it) }
    }
}