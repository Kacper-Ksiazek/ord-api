package com.ord.features.conversation.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.conversation.api.facades.ConversationCRUDFacade
import com.ord.features.conversation.api.facades.ConversationAuxiliaryFacade
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.GenerateAIInterlocutorDataRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.models.dto.ConversationDTO
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
@RequestMapping("/api/v1/conversations")
@Tag(
    name = "5. Conversations: Management",
    description = "Create and manage AI-powered conversation practice sessions with customizable scenarios and interlocutors"
)
@SecurityRequirement(name = "bearer-jwt")
class ConversationController(
    val conversationTopicFacade: ConversationAuxiliaryFacade,
    val conversationCRUDFacade: ConversationCRUDFacade,
) {
    //
    // AUXILIARY ENDPOINTS - not directly related to any conversation
    //

    @PostMapping("/suggest-topics", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(
        summary = "Suggest conversation topics",
        description = "Get AI-generated conversation topic suggestions based on language and user preferences (streaming response)"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Topic suggestions stream started successfully"
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
    fun suggestTopic(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: SuggestConversationTopicRequest
    ) = conversationTopicFacade.suggestTopics(
        userId = user.id,
        body = body
    )

    @PostMapping("/suggest-ai-interlocutor")
    @Operation(
        summary = "Generate AI interlocutor profile",
        description = "Generate a detailed AI interlocutor profile with personality, background, and conversation style"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "AI interlocutor generated successfully"
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
    fun generateAIInterlocutorData(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: GenerateAIInterlocutorDataRequest
    ) = conversationTopicFacade.generateAIInterlocutorData(
        userId = user.id,
        body = body
    )

    //
    // CRUD ENDPOINTS
    //
    @GetMapping("/")
    @Operation(
        summary = "Get all conversations",
        description = "Retrieve all conversation sessions for the authenticated user"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Conversations retrieved successfully"
        ),
        ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = [Content()]
        )
    ])
    fun getConversations(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO
    ): Mono<ResponseEntity<List<ConversationDTO>>> = conversationCRUDFacade.getManyConversations(
        userId = user.id
    )


    @GetMapping("/{conversationId}")
    @Operation(
        summary = "Get conversation by ID",
        description = "Retrieve detailed information about a specific conversation"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Conversation retrieved successfully"
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
    fun getConversationById(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Conversation ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable conversationId: UUID,
    ): Mono<ResponseEntity<ConversationDTO>> = conversationCRUDFacade.getConversationById(
        conversationId = conversationId,
        userId = user.id
    )


    @PostMapping("/")
    @Operation(
        summary = "Create a conversation",
        description = "Create a new conversation session with a topic, language, and AI interlocutor"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Conversation created successfully"
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
    fun createConversation(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: CreateConversationRequest
    ): Mono<ResponseEntity<ConversationDTO>> = conversationCRUDFacade.createConversation(
        userId = user.id,
        body = body
    )


    @DeleteMapping("/{conversationId}")
    @Operation(
        summary = "Delete a conversation",
        description = "Permanently delete a conversation and all its messages"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Conversation deleted successfully"
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
    fun deleteConversation(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(
            description = "Conversation ID",
            example = "650e8400-e29b-41d4-a716-446655440000"
        ) @PathVariable conversationId: UUID,
    ): Mono<ResponseEntity<Unit>> = conversationCRUDFacade.deleteConversation(
        conversationId = conversationId,
        userId = user.id
    )
}