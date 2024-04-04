package com.backend.ord.controllers;

import com.backend.ord.api.requests.openai.ChatGPTMessage;
import com.backend.ord.api.requests.openai.ChatGPTRole;
import com.backend.ord.api.requests.openai.OpenAIRequest;
import com.backend.ord.api.responses.openai.OpenAIResponse;
import com.backend.ord.config.RestClientConfig;
import com.backend.ord.config.properties.OpenAIProperties;
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
    private final OpenAIProperties openAIProperties;

    @GetMapping("/examples-of-usage")
    public ResponseEntity<?> index(
            @RequestParam(required = false) LanguageProficiencyLevel level,
            @RequestParam(required = false) LanguageName languageName,
            @RequestParam(required = false) String word
    ) {

        // Validate all the parameters are not null
        if (word == null) throw new BadRequestException("Word param is required");
        if (level == null) throw new BadRequestException("Level param is required");
        if (languageName == null) throw new BadRequestException("Language name param is required");

        OpenAIRequest request = OpenAIRequest.builder()
                .model(openAIProperties.getGptModel())
                .temperature(openAIProperties.getTemperature())
                .max_tokens(openAIProperties.getMaxTokens())
                .messages(new ChatGPTMessage[]{
                        ChatGPTMessage.builder()
                                .role(ChatGPTRole.user)
                                .content(
                                        String.format("Generate 3 example sentences in %s language with %s level of proficiency for the word \"%s\".",
                                                languageName.name(),
                                                level.name(),
                                                word
                                        )
                                )
                                .build(),
                        ChatGPTMessage.builder()
                                .role(ChatGPTRole.assistant)
                                .content("Give response in JSON.stringify(string[]) format")
                                .build()
                })
                .build();

        // Send the request to OpenAI
        OpenAIResponse response = restClientConfig.openAITemplate().postForObject(
                openAIProperties.getApiUrl(),
                request,
                OpenAIResponse.class
        );

        // Return 200 ok code
        return ResponseEntity.ok().body(response);
    }
}
