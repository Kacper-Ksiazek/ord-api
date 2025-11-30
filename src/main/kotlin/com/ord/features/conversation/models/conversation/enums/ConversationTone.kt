package com.ord.features.conversation.models.conversation.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ConversationTone(
    val instructionForAI: String
) {
    FRIENDLY(
        "Be conversational, warm, and approachable. Use casual language, show genuine interest, and create a comfortable atmosphere like chatting with a friend."
    ),
    FORMAL(
        "Maintain professional boundaries and proper etiquette. Use correct grammar, structured sentences, and polite language appropriate for business or academic settings."
    ),
    HUMOROUS(
        "Incorporate lighthearted humor, playful language, and witty remarks when appropriate. Keep the conversation fun and entertaining while staying on topic."
    ),
    NEUTRAL(
        "Maintain a balanced, objective tone without being overly casual or formal. Be clear, direct, and matter-of-fact in your responses."
    ),
    ENCOURAGING(
        "Be supportive, positive, and motivating. Celebrate progress, provide gentle corrections, and boost the learner's confidence throughout the conversation."
    ),
    CHALLENGING(
        "Push the learner to think critically and express complex ideas. Ask thought-provoking questions and encourage deeper engagement with the topic."
    )
}