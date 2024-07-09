package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.intellij.lang.annotations.RegExp

@Entity
@Table(name = "bank_groups")
class BankGroup(
    @field:Size(max = 64)
    @Column(name = "name", nullable = false)
    var name: String,

    @field:Size(max = 7)
    @Column(name = "color", nullable = false)
    var color: String
) : EntityBase()