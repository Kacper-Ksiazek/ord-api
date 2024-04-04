package com.backend.ord.controllers;

import com.backend.ord.api.requests.openai.OpenAIRequest;
import com.backend.ord.api.requests.openai.OpenAIRequestFactory;
import com.backend.ord.api.responses.openai.OpenAIResponse;
import com.backend.ord.config.RestClientConfig;
import com.backend.ord.enums.LanguageName;
import com.backend.ord.enums.LanguageProficiencyLevel;
import com.backend.ord.exceptions.REST.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/openai")
public class OpenAIAccessController {
    private final RestClientConfig restClientConfig;
    private final OpenAIRequestFactory OpenAIRequestFactory;

    @GetMapping("/examples-of-usage")
    public ResponseEntity<?> index(
            @RequestParam(required = false) LanguageProficiencyLevel level,
            @RequestParam(required = false) LanguageName language,
            @RequestParam(required = false) String word,
            @RequestParam(defaultValue = "3") Integer examplesCount
    ) {

        // Validate all the parameters are not null
        if (word == null) throw new BadRequestException("Missing query param: word");
        if (level == null) throw new BadRequestException("Missing query param: level");
        if (language == null) throw new BadRequestException("Missing query param: language");

        // Create the request
        OpenAIRequest request = OpenAIRequestFactory.createRequest(
                String.format("Generate %d example sentences in %s language with %s level of proficiency for the word \"%s\".",
                        examplesCount,
                        language.name(),
                        level.name(),
                        word
                ),
                "Give response in JSON.stringify(string[]) format"
        );

        // Send the request to OpenAI
        OpenAIResponse response = restClientConfig.makeOpenAIPostRequest(request);

        // Return 200 ok code
        return ResponseEntity.ok().body(response.getActualResponse());
    }
}
