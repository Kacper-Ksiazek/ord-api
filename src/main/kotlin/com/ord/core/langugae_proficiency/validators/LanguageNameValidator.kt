package com.ord.core.langugae_proficiency.validators

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class LanguageNameValidator : ConstraintValidator<ValidLanguageName, LanguageName?> {

    private val validLanguageNames = LanguageName.entries.toSet()

    override fun isValid(value: LanguageName?, context: ConstraintValidatorContext): Boolean {
        // Null is valid (optional field)
        if (value == null) return true

        // Check if the value is in the set of valid language names
        return validLanguageNames.contains(value)
    }
}
