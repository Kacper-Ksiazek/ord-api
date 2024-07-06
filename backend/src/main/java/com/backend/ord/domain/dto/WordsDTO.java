package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import com.backend.ord.domain.embedded.ExampleSentence;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Word.WordType;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WordsDTO extends DTOBase {
    private String origin;
    private String translation;
    private Boolean isBookmarked;
    private WordType type;
    private LanguageName translatedFrom;
    private LanguageName translatedTo;

    private Integer points = 0;
    private Set<ExampleSentence> exampleSentences = new HashSet<ExampleSentence>();

    private BankDTO bank;
    private UserDTO user;
}

