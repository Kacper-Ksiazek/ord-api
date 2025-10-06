package com.ord.controllers.conversations.seeders

/*

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserMapper
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationAIResponseLength
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.stereotype.Component

@Component
class ConversationSeeder(
    private val conversationRepository: ConversationRepository,
    private val userMapper: UserMapper,
) : SeederInterface<ConversationEntity> {

    fun seedOneEntity(
        authenticatedUser: MockedAuthenticatedUser,
        data: ConversationEntity = ConversationEntity(
            topic = "What small daily habit have you recently found surprisingly beneficial?",
            language = LanguageName.ENGLISH,
            proficiencyLevel = LanguageProficiencyLevel.C1,
            goal = ConversationGoal.SMALL_TALK,
            aiTone = ConversationTone.FRIENDLY,
            aiResponseLength = ConversationAIResponseLength.NORMAL,
            additionalContext = null,
            user = userMapper.toEntity(authenticatedUser.userInfo)
        )
    ): ConversationEntity = conversationRepository.save(data)
}

 */