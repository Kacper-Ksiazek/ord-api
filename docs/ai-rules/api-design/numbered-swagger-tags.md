# Group controllers under numbered, registered Swagger tags

Each controller declares a class-level `@Tag` whose name follows the `"<number>. <Domain>: <Subdomain>"` convention (e.g. `"5. Conversations: Management"`). The exact same tag name and description must be pre-registered in `OpenApiConfig.customOpenAPI()` so Swagger UI renders domains in a stable, ordered grouping rather than auto-generating tags.

## Good

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
@Tag(
    name = "5. Conversations: Management",
    description = "Create and manage AI-powered conversation practice sessions with customizable scenarios and interlocutors"
)
class ConversationController(...)
```

```kotlin
// config/OpenApiConfig.kt — the same tag is registered for ordering
Tag()
    .name("5. Conversations: Management")
    .description("AI-powered conversation practice with various scenarios and tones")
```

## Bad

```kotlin
@RestController
@RequestMapping("/api/v1/conversations")
@Tag(name = "conversations")   // unnumbered, ad-hoc name, not registered in OpenApiConfig
class ConversationController(...)
```
