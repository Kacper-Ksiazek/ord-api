package com.backend.ord.domain.entities.abstracts

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@MappedSuperclass
@Access(AccessType.FIELD)
abstract class EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant? = null

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant? = null
}
