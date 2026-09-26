package com.ord.features.ai_explainer.validators.annotations

import com.ord.features.ai_explainer.validators.FollowUpContextRequiredValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FollowUpContextRequiredValidator::class])
annotation class FollowUpContextRequired(
    val message: String = "Context is required when action is IN_THIS_CONTEXT",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
