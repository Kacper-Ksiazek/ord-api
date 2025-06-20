package com.backend.ord.features.bank_group.model

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.shared.models.IdentifiableUserResource
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "bank_groups")
data class BankGroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @field:Size(max = 64)
    @Column(name = "name", nullable = false)
    var name: String,

    @field:Size(max = 7)
    @Column(name = "color", nullable = false)
    var color: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    override var user: UserEntity,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource