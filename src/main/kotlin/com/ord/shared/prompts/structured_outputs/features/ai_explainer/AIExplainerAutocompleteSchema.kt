package com.ord.shared.prompts.structured_outputs.features.ai_explainer

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val aiExplainerAutocompleteSchema = StructuredOutputTemplate(
    name = "ai_explainer_autocomplete",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "completion" to mapOf(
                "type" to "string",
                "description" to "Only the text to append after the learner's input (no repetition of the input). Empty string when no confident completion exists."
            ),
        ),
        "required" to listOf("completion"),
        "additionalProperties" to false
    )
)
