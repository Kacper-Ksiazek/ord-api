package com.backend.ord.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public abstract class DTOBase {
    protected UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;
}
