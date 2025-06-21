package com.ord.exceptions

class OpenAIResponseIsNullException(
    message: String = "OpenAI response is null"
) : RuntimeException(message)
