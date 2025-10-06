package com.ord.features.conversation.validators.annotations

import com.ord.features.conversation.validators.ConversationAIBotAvatarIdValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ConversationAIBotAvatarIdValidator::class])
annotation class ValidConversationAIBotAvatarId(
    val message: String = "Invalid avatar ID. Must be one of the valid ConversationAIBotAvatar values",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)