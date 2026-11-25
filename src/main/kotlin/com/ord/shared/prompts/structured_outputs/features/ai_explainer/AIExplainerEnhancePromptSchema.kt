package com.ord.shared.prompts.structured_outputs.features.ai_explainer

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate

val aiExplainerEnhancePromptSchema = StructuredOutputTemplate(
    name = "ai_explainer_enhance_prompt",
    schema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "prompt" to mapOf(
                "type" to "string",
                "description" to "The learner's request rewritten to be clearer and more specific. One request, at most 250 characters, no markdown."
            ),
        ),
        "required" to listOf("prompt"),
        "additionalProperties" to false
    )
)
