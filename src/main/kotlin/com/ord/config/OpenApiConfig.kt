package com.ord.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("ORD API - Language Learning Platform")
                    .version("0.1.0")
                    .description(
                        """
                        # ORD API Documentation

                        RESTful API for a comprehensive language learning platform with AI-powered features.

                        ---

                        ## 📚 API Domains

                        | Domain | Description | Endpoints & Features |
                        |--------|-------------|----------------------|
                        | **1. Core** | Foundation services for user management and authentication | • **Authentication** - OTP-based email authentication with JWT tokens<br>• **Users** - User profile management and account initialization<br>• **Language Proficiencies** - Multi-language support with proficiency tracking |
                        | **2. Words** | Comprehensive vocabulary management with AI assistance | • **CRUD** - Create, read, update, and delete vocabulary words<br>• **AI Generation** - AI-powered word generation and enhancement<br>• **Details** - Detailed word information including examples and usage |
                        | **3. QAW** | Quickly Added Words - Rapidly collect vocabulary for later processing | • **Authenticated** - Full CRUD operations for logged-in users (bulk create, update, approve, delete)<br>• **Public** - Public endpoints for quick word submission without authentication |
                        | **4. Games** | Interactive learning games with various difficulty levels | • **General** - Start, cancel, and manage game sessions<br>• **Words Typing** - Type words quickly to improve recall and speed<br>• **Crossword** - Solve crossword puzzles with learned vocabulary<br>• **Sentences Writing** - Practice writing sentences using target words |
                        | **5. Conversations** | AI-powered conversation practice with various scenarios | • **Management** - Create conversations, suggest topics, generate AI interlocutors<br>• **Ongoing Sessions** - Send messages, get AI responses, review user messages |
                        | **6. Utility** | System utilities and health monitoring | • **Health Check** - Monitor application and database health status<br>• **AI Demo** - Test and demonstrate AI provider functionality |

                        ---

                        ## 🔐 Authentication

                        Most endpoints require JWT authentication. To get started:

                        1. **Request OTP** → `POST /api/v1/auth/otp-request` with your email
                        2. **Verify OTP** → `POST /api/v1/auth/otp-verify` with the 6-digit code from email
                        3. **Use API** → JWT token is automatically stored in cookies

                        **Alternative:** Click the **Authorize 🔒** button (top right) to manually enter your JWT token.
                        """.trimIndent()
                    )
                    .contact(
                        Contact()
                            .name("ORD API Team")
                            .email("support@ord-api.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8080")
                        .description("Development Server"),
                    Server()
                        .url("https://api.ord-platform.com")
                        .description("Production Server")
                )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearer-jwt",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT authentication token obtained via /api/v1/auth/otp-verify endpoint")
                    )
            )
            .tags(
                listOf(
                    // 1. Core Domain
                    Tag()
                        .name("1. Core: Authentication")
                        .description("OTP-based authentication endpoints for user login and logout"),
                    Tag()
                        .name("1. Core: Language Proficiencies")
                        .description("Manage user's language learning proficiencies and levels"),
                    Tag()
                        .name("1. Core: Users")
                        .description("User profile management and account initialization"),

                    // 2. Words Domain
                    Tag()
                        .name("2. Words: AI Generation")
                        .description("AI-powered word generation and enhancement"),
                    Tag()
                        .name("2. Words: CRUD")
                        .description("Create, read, update, and delete vocabulary words"),
                    Tag()
                        .name("2. Words: Details")
                        .description("Detailed word information including examples and usage"),

                    // 3. Quickly Added Words (QAW) Domain
                    Tag()
                        .name("3. QAW: Authenticated")
                        .description("Rapidly add and manage words for later processing and approval (requires authentication)"),
                    Tag()
                        .name("3. QAW: Public")
                        .description("Public endpoints for quickly added words (no authentication required)"),

                    // 4. Games Domain
                    Tag()
                        .name("4. Games: General")
                        .description("General game management endpoints"),
                    Tag()
                        .name("4. Games: Crossword")
                        .description("Crossword puzzle game variant endpoints"),
                    Tag()
                        .name("4. Games: Sentences Writing")
                        .description("Sentence writing game variant endpoints"),
                    Tag()
                        .name("4. Games: Words Typing")
                        .description("Word typing game variant endpoints"),

                    // 5. Conversations Domain
                    Tag()
                        .name("5. Conversations: Management")
                        .description("AI-powered conversation practice with various scenarios and tones"),
                    Tag()
                        .name("5. Conversations: Ongoing Sessions")
                        .description("Manage ongoing conversation sessions and messages"),

                    // 6. Utility
                    Tag()
                        .name("6. Utility: AI Demo")
                        .description("AI provider testing and demonstration endpoints"),
                    Tag()
                        .name("6. Utility: Health Check")
                        .description("Application health and status monitoring")
                )
            )
    }
}