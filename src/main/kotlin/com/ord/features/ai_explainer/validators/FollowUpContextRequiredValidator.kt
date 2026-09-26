package com.ord.features.ai_explainer.validators

import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.validators.annotations.FollowUpContextRequired
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class FollowUpContextRequiredValidator : ConstraintValidator<FollowUpContextRequired, ExplainPhraseFollowUpRequest> {

    override fun isValid(value: ExplainPhraseFollowUpRequest?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        if (value.action != ExplainPhraseFollowUpAction.IN_THIS_CONTEXT) {
            return true
        }

        return !value.context.isNullOrBlank()
    }
}
