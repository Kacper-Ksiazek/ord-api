package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StoryDTO extends DTOBase {
    private String title;
    private String content;
    private Integer numberOfTokens;
    private Map<String, String> explanations;

    private UserDTO user;
}
