package com.backend.ord.domain.entities

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "banks")
class Bank(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

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
    var bankGroup: BankGroup? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)
