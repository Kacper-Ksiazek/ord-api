package com.ord.config

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.core.type.filter.AssignableTypeFilter

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

    /**
     * Automatically scans and registers enum classes marked with @ExportToOpenAPI as reusable OpenAPI schemas.
     *
     * This uses an OPT-IN approach for security reasons:
     * - Only enums explicitly annotated with @ExportToOpenAPI will be exported
     * - Prevents accidental exposure of internal/sensitive enums
     * - Ensures that enums are exported as separate union types when generating TypeScript types
     *   using tools like openapi-typescript, instead of being inlined in DTOs
     *
     * @see ExportToOpenAPI
     */
    @Bean
    fun enumSchemaCustomizer(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            try {
                // Scan for enum classes annotated with @ExportToOpenAPI
                val enumClasses = scanForExportableEnumClasses("com.ord")

                // Ensure components and schemas are initialized
                if (openApi.components == null) {
                    openApi.components = Components()
                }
                if (openApi.components.schemas == null) {
                    openApi.components.schemas = mutableMapOf()
                }

                // Register each enum as a schema
                enumClasses.forEach { enumClass ->
                    registerEnumSchema(openApi, enumClass)
                }

                println("✅ Successfully registered ${enumClasses.size} enum schemas in OpenAPI specification (opt-in via @ExportToOpenAPI)")
            } catch (e: Exception) {
                println("⚠️  Warning: Failed to register enum schemas: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Scans the classpath for enum classes marked with @ExportToOpenAPI annotation.
     * This implements an opt-in security model where only explicitly marked enums are exported.
     */
    private fun scanForExportableEnumClasses(basePackage: String): List<Class<out Enum<*>>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)

        // Only include enums that have the @ExportToOpenAPI annotation
        scanner.addIncludeFilter(AnnotationTypeFilter(ExportToOpenAPI::class.java))

        // Additionally filter for Enum types to ensure type safety
        scanner.addIncludeFilter(AssignableTypeFilter(Enum::class.java))

        val enumClasses = mutableListOf<Class<out Enum<*>>>()

        scanner.findCandidateComponents(basePackage).forEach { beanDefinition ->
            try {
                @Suppress("UNCHECKED_CAST")
                val clazz = Class.forName(beanDefinition.beanClassName) as Class<out Enum<*>>

                // Double-check that the class has the annotation (defense in depth)
                if (clazz.isAnnotationPresent(ExportToOpenAPI::class.java)) {
                    enumClasses.add(clazz)
                }
            } catch (e: Exception) {
                println("⚠️  Warning: Could not load enum class ${beanDefinition.beanClassName}: ${e.message}")
            }
        }

        return enumClasses
    }

    /**
     * Registers a single enum class as a schema in the OpenAPI specification.
     */
    private fun registerEnumSchema(openApi: OpenAPI, enumClass: Class<out Enum<*>>) {
        val schemaName = enumClass.simpleName

        // Skip if already registered to avoid duplicates
        if (openApi.components.schemas?.containsKey(schemaName) == true) {
            return
        }

        // Extract enum values
        val enumConstants = enumClass.enumConstants
        val enumValues = enumConstants.map { it.name }

        // Extract description from @Schema annotation if present
        val description = enumClass.getAnnotation(Schema::class.java)?.description

        // Create the schema
        val schema = StringSchema().apply {
            enum = enumValues
            if (description != null && description.isNotBlank()) {
                this.description = description
            }
        }

        // Register the schema
        openApi.components.addSchemas(schemaName, schema)
    }
}