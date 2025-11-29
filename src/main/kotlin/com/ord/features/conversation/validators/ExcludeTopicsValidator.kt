package com.ord.features.conversation.validators

import com.ord.features.conversation.validators.annotations.ValidExcludeTopics
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ExcludeTopicsValidator : ConstraintValidator<ValidExcludeTopics, List<String>?> {

    override fun isValid(value: List<String>?, context: ConstraintValidatorContext): Boolean {
        // Null is valid (optional field)
        if (value == null) return true

        // Check max list size
        if (value.size > 20) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Maximum 20 topics to exclude")
                .addConstraintViolation()
            return false
        }

        // Check each topic in the list
        value.forEachIndexed { index, topic ->
            when {
                topic.isBlank() -> {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Topic at index $index cannot be blank")
                        .addConstraintViolation()
                    return false
                }
                topic.length > 500 -> {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Topic at index $index exceeds maximum length of 500 characters")
                        .addConstraintViolation()
                    return false
                }
            }
        }

        return true
    }
}
