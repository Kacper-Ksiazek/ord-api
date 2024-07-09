package com.backend.ord.domain.entities.gpt_tokens_usage

import com.backend.ord.domain.entities.Story
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.TokensUsage.StoriesGPTTokensConsumptionType
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "story_tokens_usages")
class StoryTokensUsage(
    @Column(name = "number_of_tokens", nullable = false)
    var numberOfTokens: Int,

    @Column(name = "consumption_type", columnDefinition = "stories_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var consumptionType: StoriesGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "story_id")
    var story: Story
) : EntityBase()