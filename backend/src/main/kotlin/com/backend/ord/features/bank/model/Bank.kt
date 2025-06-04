package com.backend.ord.features.bank.model

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.BankGroup
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
@Table(name = "banks")
data class Bank(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @field:Size(max = 64)
    @Column(name = "name", nullable = false, length = 64)
    var name: String,

    @field:Size(max = 255)
    @Column(name = "description", nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    override var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "group_id")
    var bankGroup: BankGroup? = null,

    @Column(name = "group_id", nullable = true, insertable = false, updatable = false)
    var bankGroupId: UUID? = bankGroup?.id,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @PostLoad
    fun populateBankGroupId(){
        bankGroupId = bankGroup?.id
    }
}
