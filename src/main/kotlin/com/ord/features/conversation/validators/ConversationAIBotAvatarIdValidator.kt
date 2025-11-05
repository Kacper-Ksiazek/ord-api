package com.ord.features.conversation.validators

import com.ord.features.conversation.models.conversation.enums.ConversationAIBotAvatar
import com.ord.features.conversation.validators.annotations.ValidConversationAIBotAvatarId
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ConversationAIBotAvatarIdValidator : ConstraintValidator<ValidConversationAIBotAvatarId, String?> {

    private val validAvatarIds = ConversationAIBotAvatar.entries.map { it.id }.toSet()

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        // Null is valid (optional field)
        if (value == null) return true

        // Check if the value is in the set of valid avatar IDs
        return validAvatarIds.contains(value)
    }
}
