package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Language.LanguageProficiencyLevel;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LanguageProficiencyDTO extends DTOBase {
    private LanguageName language;
    private LanguageProficiencyLevel proficiency;

    private UserDTO user;
}
