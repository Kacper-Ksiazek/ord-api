package com.backend.ord.shared.validators

import com.backend.ord.shared.validators.annotations.ValidStringSet
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class StringSetValidator : ConstraintValidator<ValidStringSet, Set<String>> {
    private var minSetSize: Int = 1
    private var maxSetSize: Int = 5

    private var minElementSize: Int = 1
    private var maxElementSize: Int = 255
    override fun initialize(constraintAnnotation: ValidStringSet) {
        super.initialize(constraintAnnotation)

        minSetSize = constraintAnnotation.minSetSize
        maxSetSize = constraintAnnotation.maxSetSize

        minElementSize = constraintAnnotation.minElementSize
        maxElementSize = constraintAnnotation.maxElementSize
    }

    override fun isValid(value: Set<String>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true // or false if null sets are invalid

        // Validate set size
        if (value.size in minSetSize..maxSetSize) {
            // Validate element size
            return value.all { it.length in minElementSize..maxElementSize }
        }

        return false
    }
}
