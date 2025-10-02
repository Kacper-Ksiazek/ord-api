package com.ord.features.conversation.models.enums

import com.ord.core.user.model.enums.Gender

enum class ConversationAIBotAvatar(
    val id: String,
    val gender: Gender,
    val description: String,
) {
    EMMA("AVATAR_EMMA", Gender.FEMALE, "Young woman with shoulder-length brown hair, wearing casual clothes."),
    LUCAS("AVATAR_LUCAS", Gender.MALE, "Man with short dark hair, light stubble, and a simple t-shirt."),
    SOFIA("AVATAR_SOFIA", Gender.FEMALE, "Woman with long straight black hair, wearing glasses and a blouse."),
    DANIEL("AVATAR_DANIEL", Gender.MALE, "Man with neatly combed blonde hair, clean-shaven, dressed in a suit."),
    MAYA("AVATAR_MAYA", Gender.FEMALE, "Young woman with curly red hair, freckles, and casual wear."),
    ETHAN("AVATAR_ETHAN", Gender.MALE, "Older man with short gray hair, trimmed beard, and a sweater."),
    OLIVIA("AVATAR_OLIVIA", Gender.FEMALE, "Woman with medium-length blonde hair, wearing a modern jacket."),
    JAMES("AVATAR_JAMES", Gender.MALE, "Man with dark wavy hair, wearing a collared shirt and glasses.");

    companion object {
        fun toPromptList(): String {
            return entries.joinToString(separator = "\n") { "- ${it.name} (${it.gender}) - (${it.description})" }
        }
    }
}