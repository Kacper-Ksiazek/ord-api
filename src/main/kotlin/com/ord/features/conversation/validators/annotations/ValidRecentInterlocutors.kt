package com.ord.features.conversation.validators.annotations

import com.ord.features.conversation.validators.RecentInterlocutorsValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [RecentInterlocutorsValidator::class])
annotation class ValidRecentInterlocutors(
    val message: String = "Invalid recent interlocutors list",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
