package com.ord.features.conversation.api

import com.ord.config.OpenApiSecurity

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.ConversationUserMessageAnalysisPayload
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.GetAnalysisForUserConversationMessageRequest
import com.ord.features.conversation.api.requests.GetLearningTipsForAIMessageRequest
import com.ord.features.conversation.api.requests.SaveUserConversationMessageRequest
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/conversations/ongoing")
@Tag(
    name = "5. Conversations: Ongoing Sessions",
    description = "Manage active conversation sessions with real-time AI message exchange and feedback"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class OngoingConversationController(
    private val ongoingConversationFacade: OngoingConversationFacade
) {
    @PostMapping("/ai/initialize", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Initialize conversation with AI",
        description = "Start a conversation with an AI-generated opening message (streaming response)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Conversation initialization stream started successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun initializeConversationByAI(
        @Parameter(
            description = "Conversation ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @Valid @RequestParam(required = true, name = "conversationId") conversationId: UUID,
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
    ) = ongoingConversationFacade.initializeConversationByAI(
        conversationId = conversationId,
        userId = user.id
    )

    @PostMapping("/ai/request-message", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Request AI response",
        description = "Request an AI-generated response in an ongoing conversation (streaming response)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "AI message stream started successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun requestAIMessage(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateAIConversationMessageRequest
    ) = ongoingConversationFacade.requestAIMessage(user.id, body)

    @PostMapping("/ai/generate-learning-tips")
    @Operation(
        summary = "Generate learning tips from latest AI message",
        description = "Generate structured learning tips and annotations from the most recent AI message in the conversation that doesn't have learning tips yet. The system automatically identifies the appropriate message."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Learning tips generated successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found or no AI message without learning tips exists",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun generateLearningTipsForAIMessage(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: GetLearningTipsForAIMessageRequest
    ): Mono<ResponseEntity<AIMessageLearningTips>> =
        ongoingConversationFacade.generateLearningTipsForAIMessage(user.id, user.nativeLanguage!!, body)

    @PostMapping("/user/save-message")
    @Operation(
        summary = "Save user message",
        description = "Save a user message to a conversation"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User message saved successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun saveUserMessage(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: SaveUserConversationMessageRequest
    ): Mono<ResponseEntity<ConversationMessageDTO>> =
        ongoingConversationFacade.saveUserMessage(user.id, body)

    @PostMapping("/user/generate-analysis")
    @Operation(
        summary = "Generate analysis for user message",
        description = "Generate AI-powered grammar and language analysis for an existing user message"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Analysis generated successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation or message not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            )
        ]
    )
    fun generateAnalysis(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: GetAnalysisForUserConversationMessageRequest
    ): Mono<ResponseEntity<ConversationUserMessageAnalysisPayload>> =
        ongoingConversationFacade.generateAnalysisForUserMessage(user.id, body)

}
