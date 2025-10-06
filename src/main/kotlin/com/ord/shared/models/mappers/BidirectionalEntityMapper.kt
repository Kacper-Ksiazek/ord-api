package com.ord.shared.models.mappers

interface BidirectionalEntityMapper<Entity, DTO>: UnidirectionalEntityMapper<Entity, DTO> {
    fun toEntity(dto: DTO): Entity

    fun toEntityOrNull(dto: DTO?): Entity? {
        return dto?.let { toEntity(it) }
    }
}