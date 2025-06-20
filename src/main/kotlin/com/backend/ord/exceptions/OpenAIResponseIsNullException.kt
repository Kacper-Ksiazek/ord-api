package com.backend.ord.exceptions

class OpenAIResponseIsNullException(
    message: String = "OpenAI response is null"
) : RuntimeException(message)
