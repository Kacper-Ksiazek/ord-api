package com.ord.shared.validators.annotations

import com.ord.shared.validators.StringSetValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [StringSetValidator::class])
annotation class ValidStringSet(
    val minSetSize: Int = 1,
    val maxSetSize: Int = 5,

    val minElementSize: Int = 1,
    val maxElementSize: Int = 255,

    val message: String = "Invalid string set",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
