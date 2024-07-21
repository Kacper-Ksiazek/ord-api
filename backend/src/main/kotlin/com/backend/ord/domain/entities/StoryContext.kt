package com.backend.ord.domain.entities

import com.backend.ord.enums.StoryContextType
import jakarta.persistence.*
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.*
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "story_contexts")
class StoryContext(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

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
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "story_id")
    var story: Story,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)