package com.backend.ord.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_groups")
public class BankGroup extends EntityBase {
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "color", nullable = false, length = 6)
    private String color;
}