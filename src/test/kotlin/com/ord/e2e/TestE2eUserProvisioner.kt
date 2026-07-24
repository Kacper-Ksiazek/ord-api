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

@DisplayName("- E2E user provisioner")
class TestE2eUserProvisioner @Autowired constructor(
    private val e2eUserProvisioner: E2eUserProvisioner,
    private val userRepository: UserRepository,
    private val languageProficiencyRepository: LanguageProficiencyRepository,
) : TestcontainersConfig() {

    @Test
    fun `provisionWorker is idempotent`() {
        val email = "e2e-ci-w0@ord.test"

        e2eUserProvisioner.provisionWorker(email).block()
        e2eUserProvisioner.provisionWorker(email).block()

        val user = userRepository.findByEmail(email).block().shouldNotBeNull()
        user.id shouldBe E2eAccountIds.userId(email)

        val proficiency = languageProficiencyRepository
            .findUserProficiencyInLanguage(user.id!!, LanguageName.ENGLISH.name)
            .block()
            .shouldNotBeNull()

        proficiency.level shouldBe LanguageProficiencyLevel.B1
    }

    @Test
    fun `provisionAllWorkers upserts missing worker accounts`() {
        val email = "e2e-ci-w3@ord.test"
        val userId = E2eAccountIds.userId(email)

        languageProficiencyRepository.findUserProficiencyInLanguage(userId, LanguageName.ENGLISH.name)
            .flatMap { languageProficiencyRepository.delete(it) }
            .then(userRepository.deleteById(userId))
            .block()

        userRepository.findByEmail(email).block() shouldBe null

        val users = e2eUserProvisioner.provisionAllWorkers().collectList().block()!!
        users.size shouldBe 4

        val restored = userRepository.findByEmail(email).block().shouldNotBeNull()
        restored.id shouldBe userId
    }
}
