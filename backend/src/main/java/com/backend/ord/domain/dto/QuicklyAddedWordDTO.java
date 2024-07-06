package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import com.backend.ord.enums.Language.LanguageName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuicklyAddedWordDTO extends DTOBase {
    private String typedWord;
    private LanguageName typedInLanguage;

    private UserDTO user;
}
