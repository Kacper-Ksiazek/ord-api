package com.backend.ord.domain.entities.gpt_tokens_usage

import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "word_tokens_usages")
class WordTokensUsage(
    @field:Size(min = 1)
    @Column(name = "number_of_generations", nullable = false)
    var numberOfGenerations: Int = 1,

    @field:Min(0)
    @Column(name = "number_of_tokens", nullable = false)
    var numberOfTokens: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_type", columnDefinition = "words_gpt_tokens_consumption_type(0, 0) not null")
    var consumptionType: WordsGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "word_id")
    var words: Word
) : EntityBase()