package com.backend.ord.domain.entities;

import com.backend.ord.enums.GptTokensConsumptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "gpt_tokens_consumption")
public class GptTokensConsumption extends EntityBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "number_of_tokens", nullable = false)
    private Integer numberOfTokens;

    @Column(name = "consumption_type", columnDefinition = "gpt_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    private GptTokensConsumptionType consumptionType;
}