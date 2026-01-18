package com.ord.features.conversation.services.impl

import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsEntity
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsMapper
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackMapper
import com.ord.features.conversation.repositories.ConversationAIMessageLearningTipsRepository
import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.repositories.ConversationUserMessageFeedbackRepository
import com.ord.features.conversation.services.ConversationMessageService
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class ConversationMessageServiceImpl(
    private val conversationMessageRepository: ConversationMessageRepository,
    private val conversationUserMessageFeedbackRepository: ConversationUserMessageFeedbackRepository,
    private val conversationAIMessageLearningTipsRepository: ConversationAIMessageLearningTipsRepository,
    private val feedbackMapper: ConversationUserMessageFeedbackMapper,
    private val learningTipsMapper: ConversationAIMessageLearningTipsMapper,
    private val databaseClient: DatabaseClient,
) : ConversationMessageService {
    override fun createMessage(
        conversationId: UUID,
        sender: ConversationMessageSender,
        content: String,
        messageOrder: Int
    ): Mono<ConversationMessageEntity> {
        return conversationMessageRepository.save(
            ConversationMessageEntity(
                conversationId = conversationId,
                sender = sender,
                content = content,
                messageOrder = messageOrder
            )
        )
    }

    override fun createMessageWithFeedback(
        conversationId: UUID,
        messageOrder: Int,
        content: String,
        aiFeedback: ReviewedUserConversationMessage
    ): Mono<ConversationMessageEntity> {
        return conversationMessageRepository
            .save(
                ConversationMessageEntity(
                    conversationId = conversationId,
                    sender = ConversationMessageSender.USER,
                    content = content,
                    messageOrder = messageOrder,
                )
            )
            .flatMap { message ->
                conversationUserMessageFeedbackRepository
                    .save(
                        ConversationUserMessageFeedbackEntity(
                            tutorComment = aiFeedback.tutorComment,
                            grammar = aiFeedback.grammar,
                            vocabulary = aiFeedback.vocabulary,
                            answerLength = aiFeedback.answerLength,
                            naturalness = aiFeedback.naturalness,
                            coherenceWithContext = aiFeedback.coherenceWithContext,
                            registerAppropriate = aiFeedback.registerAppropriate,
                            mistakes = feedbackMapper.serializeMistakes(aiFeedback.mistakes),
                            strengths = feedbackMapper.serializeStrengths(aiFeedback.strengths),
                            suggestions = feedbackMapper.serializeSuggestions(aiFeedback.suggestions),
                            messageId = message.id!!,
                        )
                    )
                    .map { _ -> message }
            }
    }

    override fun saveUserMessageWithId(
        messageId: UUID,
        conversationId: UUID,
        content: String,
        messageOrder: Int
    ): Mono<ConversationMessageEntity> {
        val createdAt = Instant.now()
        return databaseClient.sql(
            """
            INSERT INTO conversation_messages (id, content, message_order, sender, conversation_id, created_at)
            VALUES (:id, :content, :messageOrder, :sender, :conversationId, :createdAt)
        """
        )
            .bind("id", messageId)
            .bind("content", content)
            .bind("messageOrder", messageOrder)
            .bind("sender", ConversationMessageSender.USER.name)
            .bind("conversationId", conversationId)
            .bind("createdAt", createdAt)
            .fetch()
            .rowsUpdated()
            .thenReturn(
                ConversationMessageEntity(
                    id = messageId,
                    content = content,
                    messageOrder = messageOrder,
                    sender = ConversationMessageSender.USER,
                    conversationId = conversationId,
                    createdAt = createdAt
                )
            )
    }

    override fun saveFeedbackForExistingMessage(
        messageId: UUID,
        aiFeedback: ReviewedUserConversationMessage
    ): Mono<ConversationMessageEntity> {
        return conversationUserMessageFeedbackRepository
            .save(
                ConversationUserMessageFeedbackEntity(
                    tutorComment = aiFeedback.tutorComment,
                    grammar = aiFeedback.grammar,
                    vocabulary = aiFeedback.vocabulary,
                    answerLength = aiFeedback.answerLength,
                    naturalness = aiFeedback.naturalness,
                    coherenceWithContext = aiFeedback.coherenceWithContext,
                    registerAppropriate = aiFeedback.registerAppropriate,
                    mistakes = feedbackMapper.serializeMistakes(aiFeedback.mistakes),
                    strengths = feedbackMapper.serializeStrengths(aiFeedback.strengths),
                    suggestions = feedbackMapper.serializeSuggestions(aiFeedback.suggestions),
                    messageId = messageId,
                )
            )
            .flatMap { feedback ->
                conversationMessageRepository
                    .findById(messageId)
                    .flatMap { message ->
                        conversationMessageRepository.save(
                            message.copy(
                                feedbackId = feedback.id
                            )
                        )
                    }
            }
    }

    override fun saveLearningTipsForExistingMessage(
        messageId: UUID,
        learningTips: AIMessageLearningTips
    ): Mono<ConversationMessageEntity> {
        return conversationAIMessageLearningTipsRepository
            .save(
                ConversationAIMessageLearningTipsEntity(
                    grammarTips = learningTipsMapper.serializeGrammarTips(learningTips.grammarTips),
                    vocabularyTips = learningTipsMapper.serializeVocabularyTips(learningTips.vocabularyTips),
                    phraseTips = learningTipsMapper.serializePhraseTips(learningTips.phraseTips),
                    messageId = messageId,
                )
            )
            .then(conversationMessageRepository.findById(messageId))
    }
}
