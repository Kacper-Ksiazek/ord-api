package com.backend.ord.domain.embedded;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExampleSentence {
    private String sentence;
    private String translation;
}
