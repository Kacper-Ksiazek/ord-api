package com.ord.core.langugae_proficiency.validators.annotations

import com.ord.core.langugae_proficiency.validators.LanguageProficiencyLevelValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LanguageProficiencyLevelValidator::class])
annotation class ValidLanguageProficiencyLevel(
    val message: String = "Invalid proficiency level. Must be one of the valid LanguageProficiencyLevel values",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
