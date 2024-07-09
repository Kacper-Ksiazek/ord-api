package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "user_sessions")
class UserSession(
    @Column(name = "token", nullable = false, updatable = false, unique = true)
    var token: String,

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User
) : EntityBase()
