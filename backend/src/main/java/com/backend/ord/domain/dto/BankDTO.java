package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BankDTO extends DTOBase {
    private String name;
    private String description;

    private UserDTO user;
    private BankGroupDTO group;
}
