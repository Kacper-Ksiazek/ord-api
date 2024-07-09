package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "banks")
class Bank(
    @field:Size(max = 64)
    @Column(name = "name", nullable = false, length = 64)
    var name: String,

    @field:Size(max = 255)
    @Column(name = "description", nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "group_id")
    var group: BankGroup? = null
) : EntityBase()
