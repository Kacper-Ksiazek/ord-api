package com.ord.features.conversation.validators.annotations

import com.ord.features.conversation.validators.ExcludeTopicsValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ExcludeTopicsValidator::class])
annotation class ValidExcludeTopics(
    val message: String = "Invalid exclude topics list",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
