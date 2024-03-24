package com.backend.ord.domain.dto;

import java.time.Instant;
import java.util.UUID;

public abstract class DTOBase {
    UUID id;
    Instant createdAt;
    Instant updatedAt;
}
