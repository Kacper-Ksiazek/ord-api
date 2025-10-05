package com.ord.features.conversation.models.enums

import com.ord.core.user.model.enums.Gender

enum class ConversationAIBotAvatar(
    val id: String,
    val gender: Gender,
    val description: String,
) {
    AVATAR_ALPHA(
        "AVATAR_ALPHA",
        Gender.FEMALE,
        "Young woman with shoulder-length brown hair, wearing casual clothes, smiling warmly with bright, attentive eyes."
    ),
    AVATAR_BETA(
        "AVATAR_BETA",
        Gender.MALE,
        "Man with short dark hair, light stubble, and a simple t-shirt, with a relaxed grin and friendly expression."
    ),
    AVATAR_GAMMA(
        "AVATAR_GAMMA",
        Gender.FEMALE,
        "Woman with long straight black hair, wearing glasses and a blouse, with a thoughtful gaze and a gentle smile."
    ),
    AVATAR_DELTA(
        "AVATAR_DELTA",
        Gender.MALE,
        "Man with neatly combed blonde hair, clean-shaven, dressed in a suit, showing a confident look with a composed, serious expression."
    ),
    AVATAR_EPSILON(
        "AVATAR_EPSILON",
        Gender.FEMALE,
        "Young woman with curly red hair, freckles, and casual wear, with a playful smile and lively, curious eyes."
    ),
    AVATAR_ZETA(
        "AVATAR_ZETA",
        Gender.MALE,
        "Older man with short gray hair, trimmed beard, and a sweater, with a kind smile and wise, calm demeanor."
    ),
    AVATAR_ETA(
        "AVATAR_ETA",
        Gender.FEMALE,
        "Woman with medium-length blonde hair, wearing a modern jacket, with a confident smile and warm, approachable eyes."
    ),
    AVATAR_THETA(
        "AVATAR_THETA",
        Gender.MALE,
        "Man with dark wavy hair, wearing a collared shirt and glasses, with a focused expression and a slight, knowing smile."
    );

    companion object {
        fun toPromptList(): String {
            return entries.joinToString(separator = "\n") { "- ${it.name} (${it.gender}) - (${it.description})" }
        }
    }
}