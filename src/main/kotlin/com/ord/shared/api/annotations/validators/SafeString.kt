package com.ord.shared.api.annotations.validators

import jakarta.validation.Constraint
import jakarta.validation.OverridesAttribute
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [])
@NotBlank
@Size(min = 1, max = 255)     // defaults; will be overridden
@ReportAsSingleViolation
annotation class SafeString(
    @get:OverridesAttribute(constraint = Size::class, name = "min")
    val min: Int = 1,

    @get:OverridesAttribute(constraint = Size::class, name = "max")
    val max: Int = 255,

    val fieldName: String,

    // used when @ReportAsSingleViolation is present
    val message: String = "{fieldName} must be between {min} and {max} characters",

    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

