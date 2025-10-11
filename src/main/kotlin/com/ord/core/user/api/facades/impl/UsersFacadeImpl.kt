package com.ord.core.user.api.facades.impl

import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.security.UserRepository
import com.ord.core.user.api.facades.UsersFacade
import com.ord.core.user.api.requests.InitUserAccountRequest
import com.ord.core.user.api.responses.MeResponse
import com.ord.core.user.model.UserDTO
import com.ord.exceptions.REST.NotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class UsersFacadeImpl(
    private val languageProficiencyService: LanguageProficiencyService,
    private val userRepository: UserRepository,
) : UsersFacade {
    override fun me(
        user: UserDTO,
    ): Mono<ResponseEntity<MeResponse>> {
        return languageProficiencyService
            .findAll(user.id)
            .collectMap({ it.language }, { it.level })
            .map { languages ->
                MeResponse(
                    name = user.name,
                    email = user.email,
                    nativeLanguage = user.nativeLanguage,
                    selectedLearningLanguage = user.selectedLearningLanguage
                )
            }
            .map { ResponseEntity.ok(it) }
    }

    override fun initUserAccount(
        userId: UUID,
        body: InitUserAccountRequest
    ): Mono<ResponseEntity<MeResponse>> {
        return userRepository
            .findById(userId)
            .switchIfEmpty(Mono.error(NotFoundException("User not found")))
            .flatMap { user ->
                user.name = body.name
                user.nativeLanguage = body.nativeLanguage
                user.selectedLearningLanguage = body.selectedLearningLanguage
                user.isAccountInitialized = true

                userRepository.save(user)
            }
            .flatMap { savedUser ->
                Flux.fromIterable(body.languageProficiencies)
                    .flatMap { proficiency ->
                        languageProficiencyService.create(
                            userId = savedUser.id!!,
                            language = proficiency.language,
                            level = proficiency.level,
                            generativeContentLanguage = proficiency.generativeContentLanguage
                        )
                    }
                    .then(Mono.just(savedUser))
            }
            .map { user ->
                MeResponse(
                    name = user.name,
                    email = user.email,
                    nativeLanguage = user.nativeLanguage,
                    selectedLearningLanguage = user.selectedLearningLanguage
                )
            }
            .map { ResponseEntity.ok(it) }
    }
}