package com.ord.e2e

import com.ord.controllers.bases.TestcontainersConfig
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("- E2E worker Flyway seed (V22)")
class TestE2eWorkerFlywaySeed @Autowired constructor(
    private val userRepository: UserRepository,
    private val languageProficiencyRepository: LanguageProficiencyRepository,
) : TestcontainersConfig() {

    private val workerEmails = listOf(
        "e2e-ci-w0@ord.test",
        "e2e-ci-w1@ord.test",
        "e2e-ci-w2@ord.test",
        "e2e-ci-w3@ord.test",
    )

    @Test
    fun `Flyway V22 seeds all worker users with ENGLISH B1 proficiency`() {
        workerEmails.forEach { email ->
            val user = userRepository.findByEmail(email).block().shouldNotBeNull()
            user.isAccountInitialized shouldBe true
            user.nativeLanguage shouldBe LanguageName.POLISH
            user.selectedLearningLanguage shouldBe LanguageName.ENGLISH
            user.id shouldBe E2eAccountIds.userId(email)

            val proficiency = languageProficiencyRepository
                .findUserProficiencyInLanguage(user.id!!, LanguageName.ENGLISH.name)
                .block()
                .shouldNotBeNull()

            proficiency.level shouldBe LanguageProficiencyLevel.B1
            proficiency.translateTo shouldBe LanguageName.POLISH
            proficiency.generativeContentLanguage shouldBe LanguageName.ENGLISH
            proficiency.id shouldBe E2eAccountIds.proficiencyId(email)
        }
    }
}
