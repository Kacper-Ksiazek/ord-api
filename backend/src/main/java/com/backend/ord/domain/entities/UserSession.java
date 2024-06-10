package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "user_sessions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSession extends EntityBase {
    @Column(name = "token", nullable = false, updatable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
