package com.ord.e2e

import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.user.model.UserEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class E2eUserProvisioner(
    private val userRepository: UserRepository,
    private val languageProficiencyRepository: LanguageProficiencyRepository,
    private val e2eSeederProperties: E2eSeederProperties,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    fun provisionAllWorkers(): Flux<UserEntity> =
        Flux.fromIterable(e2eSeederProperties.workerEmails())
            .flatMap { email -> provisionWorker(email) }

    fun provisionWorker(email: String): Mono<UserEntity> {
        val workerIndex = email.substringAfter(e2eSeederProperties.emailPrefix).substringBefore("@")
        val userId = E2eAccountIds.userId(email)

        val user = UserEntity(
            id = userId,
            name = "E2E Worker $workerIndex",
            email = email,
            nativeLanguage = LanguageName.POLISH,
            selectedLearningLanguage = LanguageName.ENGLISH,
            isAccountInitialized = true,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )

        return userRepository.findByEmail(email)
            .flatMap { existing ->
                if (existing == null) {
                    Mono.empty()
                } else {
                    val updated = existing.copy(
                        name = user.name,
                        nativeLanguage = user.nativeLanguage,
                        selectedLearningLanguage = user.selectedLearningLanguage,
                        isAccountInitialized = true,
                        updatedAt = Instant.now(),
                    )
                    userRepository.save(updated)
                }
            }
            .switchIfEmpty(
                r2dbcEntityTemplate.insert(UserEntity::class.java)
                    .using(user)
                    .map { user },
            )
            .flatMap { savedUser ->
                ensureEnglishProficiency(savedUser).thenReturn(savedUser)
            }
    }

    private fun ensureEnglishProficiency(user: UserEntity): Mono<LanguageProficiencyEntity> {
        val userId = user.id ?: return Mono.empty()
        val email = user.email
        val proficiencyId = E2eAccountIds.proficiencyId(email)

        val proficiency = LanguageProficiencyEntity(
            id = proficiencyId,
            language = LanguageName.ENGLISH,
            level = LanguageProficiencyLevel.B1,
            translateTo = LanguageName.POLISH,
            generativeContentLanguage = LanguageName.ENGLISH,
            userId = userId,
            createdAt = Instant.now(),
        )

        return languageProficiencyRepository.findUserProficiencyInLanguage(userId, LanguageName.ENGLISH.name)
            .flatMap { existing ->
                if (existing == null) {
                    Mono.empty()
                } else {
                    val updated = existing.copy(
                        level = proficiency.level,
                        translateTo = proficiency.translateTo,
                        generativeContentLanguage = proficiency.generativeContentLanguage,
                    )
                    languageProficiencyRepository.save(updated)
                }
            }
            .switchIfEmpty(
                r2dbcEntityTemplate.insert(LanguageProficiencyEntity::class.java)
                    .using(proficiency)
                    .map { proficiency },
            )
    }
}
