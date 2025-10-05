package com.ord.core.langugae_proficiency.validators.annotations

import com.ord.core.langugae_proficiency.validators.LanguageNameValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LanguageNameValidator::class])
annotation class ValidLanguageName(
    val message: String = "Invalid language name. Must be one of the valid LanguageName values",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
