package com.ord.core.langugae_proficiency.validators

import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageProficiencyLevel
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class LanguageProficiencyLevelValidator : ConstraintValidator<ValidLanguageProficiencyLevel, LanguageProficiencyLevel?> {

    private val validProficiencyLevels = LanguageProficiencyLevel.entries.toSet()

    override fun isValid(value: LanguageProficiencyLevel?, context: ConstraintValidatorContext): Boolean {
        // Null is valid (optional field)
        if (value == null) return true

        // Check if the value is in the set of valid proficiency levels
        return validProficiencyLevels.contains(value)
    }
}
