package com.backend.ord.domain.persistance.entities

import com.backend.ord.domain.persistance.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.persistance.StoryContextType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "story_contexts")
class StoryContext(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override val id: UUID = UUID.randomUUID(),

    @Column(name = "title", length = 64)
    var title: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: StoryContextType,

    @Column(name = "prompt")
    var prompt: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    override var user: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
): IdentifiableUserResource