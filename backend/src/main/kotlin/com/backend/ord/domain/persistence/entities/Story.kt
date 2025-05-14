package com.backend.ord.domain.persistence.entities

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.shared.models.IdentifiableUserResource
import jakarta.persistence.*
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.*
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "stories")
data class Story(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @field:Size(max = 64)
    @Column(name = "title", nullable = false, length = 64)
    var title: String,

    @Column(name = "content", nullable = false, length = Int.MAX_VALUE)
    var content: String,

    // In the following `Map` structure keys are the words and values are the explanations of their meanings, followed by a brief explaining how they contribute to the story.
    @Column(name = "explanations", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    var explanations: MutableMap<String, String> = mutableMapOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    override var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "story_context_id")
    var storyContext: StoryContext,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource