package com.ord.features.conversation.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
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
@SecurityRequirement(name = "bearer-jwt")
class OngoingConversationController(
    val ongoingConversationFacade: OngoingConversationFacade
) {
    @PostMapping("/initialize-by-ai", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Initialize conversation with AI",
        description = "Start a conversation with an AI-generated opening message (streaming response)"
    )
    @ApiResponses(value = [
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
    ])
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


    @PostMapping("/request-ai-message", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Request AI response",
        description = "Request an AI-generated response in an ongoing conversation (streaming response)"
    )
    @ApiResponses(value = [
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
    ])
    fun requestAIMessage(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateAIConversationMessageRequest
    ) = ongoingConversationFacade.requestAIMessage(user.id, body)


    @PostMapping("/generate-feedback")
    @Operation(
        summary = "Generate feedback for user message",
        description = "Generate AI-powered grammar and language feedback for an existing user message"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Feedback generated successfully"
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
    ])
    fun generateFeedback(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: ReviewUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>> =
        ongoingConversationFacade.generateFeedbackForMessage(user.id, body)
}