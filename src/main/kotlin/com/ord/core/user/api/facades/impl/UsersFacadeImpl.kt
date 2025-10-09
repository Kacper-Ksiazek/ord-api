package com.ord.core.user.api.facades.impl

import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.api.facades.UsersFacade
import com.ord.core.user.api.responses.MeResponse
import com.ord.core.user.model.UserDTO
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UsersFacadeImpl(
    private val languageProficiencyService: LanguageProficiencyService,
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
}