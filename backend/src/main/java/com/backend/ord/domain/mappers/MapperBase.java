package com.backend.ord.domain.mappers;

public interface MapperBase<Entity, DTO> {
    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);
}
