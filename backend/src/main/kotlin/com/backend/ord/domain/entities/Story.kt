package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "stories")
class Story(
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
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    var user: User,
) : EntityBase()