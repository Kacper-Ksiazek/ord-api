package com.ord.features.conversation.models.conversation.enums

import com.ord.core.user.model.enums.Gender
import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ConversationAIBotAvatar(
    val id: String,
    val gender: Gender,
    val description: String,
) {
    AVATAR_DEFAULT(
        "AVATAR_DEFAULT",
        Gender.MALE,
        "Friendly humanoid robot with a sleek metallic design, glowing blue LED eyes, and a welcoming digital smile display on its faceplate."
    ),
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
        "Attractive blonde woman in early 20s with wavy golden hair, stylish  sunglasses resting on her head, confident pose, wearing trendy casual  clothing, with a captivating smile and expressive, alluring eyes."
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
        /**
         * Returns a list of selectable avatars, excluding AVATAR_DEFAULT
         * which is only used for fallback scenarios.
         */
        fun getSelectableAvatars(): List<ConversationAIBotAvatar> {
            return entries.filter { it != AVATAR_DEFAULT }
        }

        /**
         * Returns a list of valid avatar enum names for structured output schemas.
         * Excludes AVATAR_DEFAULT as it's only used for fallback scenarios.
         */
        fun toSchemaEnumList(): List<String> {
            return getSelectableAvatars().map { it.name }
        }
    }
}