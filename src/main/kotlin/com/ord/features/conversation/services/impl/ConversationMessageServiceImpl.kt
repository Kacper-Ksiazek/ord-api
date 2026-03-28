package com.ord.features.conversation.services.impl

import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsEntity
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsMapper
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisMapper
import com.ord.features.conversation.repositories.ConversationAIMessageLearningTipsRepository
import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.repositories.ConversationUserMessageAnalysisRepository
import com.ord.features.conversation.services.ConversationMessageService
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class ConversationMessageServiceImpl(
    private val conversationMessageRepository: ConversationMessageRepository,
    private val conversationUserMessageAnalysisRepository: ConversationUserMessageAnalysisRepository,
    private val conversationAIMessageLearningTipsRepository: ConversationAIMessageLearningTipsRepository,
    private val analysisMapper: ConversationUserMessageAnalysisMapper,
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
        ).flatMap { message ->
            if (sender == ConversationMessageSender.USER) {
                bumpConversationUpdatedAt(conversationId).thenReturn(message)
            } else {
                Mono.just(message)
            }
        }
    }

    override fun createMessageWithAnalysis(
        conversationId: UUID,
        messageOrder: Int,
        content: String,
        aiAnalysis: ReviewedUserConversationMessage
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
                conversationUserMessageAnalysisRepository
                    .save(
                        ConversationUserMessageAnalysisEntity(
                            tutorComment = aiAnalysis.tutorComment,
                            correctedMessage = aiAnalysis.correctedMessage,
                            grammar = aiAnalysis.grammar,
                            vocabulary = aiAnalysis.vocabulary,
                            naturalness = aiAnalysis.naturalness,
                            coherenceWithContext = aiAnalysis.coherenceWithContext,
                            mistakes = analysisMapper.serializeMistakes(aiAnalysis.mistakes),
                            strengths = analysisMapper.serializeStrengths(aiAnalysis.strengths),
                            suggestions = analysisMapper.serializeSuggestions(aiAnalysis.suggestions),
                            messageId = message.id!!,
                        )
                    )
                    .flatMap { _ -> bumpConversationUpdatedAt(conversationId).thenReturn(message) }
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
            .flatMap {
                bumpConversationUpdatedAt(conversationId).thenReturn(
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
    }

    override fun saveAnalysisForExistingMessage(
        messageId: UUID,
        aiAnalysis: ReviewedUserConversationMessage
    ): Mono<Void> {
        return conversationUserMessageAnalysisRepository
            .save(
                ConversationUserMessageAnalysisEntity(
                    tutorComment = aiAnalysis.tutorComment,
                    correctedMessage = aiAnalysis.correctedMessage,
                    grammar = aiAnalysis.grammar,
                    vocabulary = aiAnalysis.vocabulary,
                    naturalness = aiAnalysis.naturalness,
                    coherenceWithContext = aiAnalysis.coherenceWithContext,
                    mistakes = analysisMapper.serializeMistakes(aiAnalysis.mistakes),
                    strengths = analysisMapper.serializeStrengths(aiAnalysis.strengths),
                    suggestions = analysisMapper.serializeSuggestions(aiAnalysis.suggestions),
                    messageId = messageId,
                )
            )
            .then()
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

    private fun bumpConversationUpdatedAt(conversationId: UUID): Mono<Void> {
        return databaseClient.sql("UPDATE conversations SET updated_at = :now WHERE id = :id")
            .bind("now", Instant.now())
            .bind("id", conversationId)
            .fetch()
            .rowsUpdated()
            .then()
    }
}
