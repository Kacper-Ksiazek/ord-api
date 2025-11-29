package com.ord.features.conversation.validators

import com.ord.features.conversation.api.requests.dto.RecentInterlocutorInfo
import com.ord.features.conversation.validators.annotations.ValidRecentInterlocutors
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class RecentInterlocutorsValidator : ConstraintValidator<ValidRecentInterlocutors, List<RecentInterlocutorInfo>?> {

    override fun isValid(value: List<RecentInterlocutorInfo>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        if (value.size > 10) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Maximum 10 recent interlocutors allowed")
                .addConstraintViolation()
            return false
        }

        return true
    }
}
