package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanksUsedInGameDTO {
    private UUID id;

    private GameDTO game;
    private BankDTO bank;
}
