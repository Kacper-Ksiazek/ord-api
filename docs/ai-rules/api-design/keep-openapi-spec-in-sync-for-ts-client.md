# Re-export the OpenAPI spec after any API contract change

The frontend's TypeScript types are generated from `openapi.json` and published as the `types-package` (`@kacper-ksiazek/ord-api-types`, on GitHub Packages). Whenever you add/rename an endpoint, change a request/response DTO, or alter an enum, re-export the spec so the contract stays accurate: start the app and run `make openapi` (which calls `export-openapi-spec.sh` against `/v3/api-docs`). Enums that should appear as standalone TS union types must be annotated with `@ExportToOpenAPI`.

## Good

```bash
# After changing CreateConversationRequest or ConversationDTO:
mvn spring-boot:run            # start the API
make openapi                   # exports openapi.json from /v3/api-docs
# then regenerate / republish the types-package from openapi.json
```

```kotlin
// Enums consumed by the TS client are opted in so they are emitted as reusable schemas
@ExportToOpenAPI
@Schema(description = "Supported conversation tones")
enum class ConversationTone { FORMAL, CASUAL, FRIENDLY }
```

## Bad

```kotlin
// Added a new required field but never re-exported the spec.
data class CreateConversationRequest(
    val topic: String,
    val difficulty: Int,   // new field missing from openapi.json -> TS client out of sync, frontend breaks at runtime
)
```
