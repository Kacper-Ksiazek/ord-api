package com.ord.features.gpt_tokens_usage_log.variants.story_tokens_usage.model

import com.ord.core.user.model.UserEntity
import com.ord.features.story.models.entities.StoryEntity
import com.ord.features.gpt_tokens_usage_log.variants.story_tokens_usage.model.enums.StoriesGPTTokensConsumptionType
import com.ord.shared.models.IdentifiableUserResource
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "story_tokens_usages")
data class StoryTokensUsageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "number_of_tokens", nullable = false)
    var numberOfTokens: Int,

    @Column(name = "consumption_type", columnDefinition = "stories_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var consumptionType: StoriesGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    override var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "story_id")
    var story: StoryEntity,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
): IdentifiableUserResource