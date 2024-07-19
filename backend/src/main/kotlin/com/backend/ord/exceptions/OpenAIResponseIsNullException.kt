package com.backend.ord.exceptions

import java.lang.RuntimeException

class OpenAIResponseIsNullException(
    message: String = "OpenAI response is null"
) : RuntimeException(message) {
}
