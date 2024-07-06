package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProgressDTO extends DTOBase {
    private Integer pointsObtained;

    private UserDTO user;
    private GameDTO game;
}
