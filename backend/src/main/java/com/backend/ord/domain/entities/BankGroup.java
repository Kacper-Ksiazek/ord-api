package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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