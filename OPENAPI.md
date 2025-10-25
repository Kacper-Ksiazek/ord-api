# OpenAPI Documentation

This document describes how to use the OpenAPI specification for the ORD API.

## Overview

The ORD API now includes comprehensive OpenAPI 3.0 documentation using SpringDoc OpenAPI. This provides:

- **Interactive API Documentation** via Swagger UI
- **Machine-readable API Specification** in JSON format
- **TypeScript Interface Generation** for frontend development
- **API Client Generation** for various languages

## Accessing the API Documentation

### Swagger UI (Interactive Documentation)

Once the application is running, you can access the interactive Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

**Authentication Required:** Swagger UI is protected with HTTP Basic Authentication.

Default credentials (can be customized via environment variables):
- **Username:** `admin`
- **Password:** `admin`

To customize credentials, set these environment variables:
```bash
export SWAGGER_USERNAME=your_username
export SWAGGER_PASSWORD=your_password
```

Features:
- Browse all API endpoints organized by domain tags
- View request/response schemas with examples
- Test endpoints directly from the browser
- JWT authentication support for API endpoints (click "Authorize" button)

### OpenAPI Specification (JSON)

The raw OpenAPI specification is available at:

```
http://localhost:8080/v3/api-docs
```

**Note:** This endpoint also requires HTTP Basic Authentication (same credentials as Swagger UI).

This endpoint returns the complete OpenAPI 3.0 specification in JSON format.

## Exporting the OpenAPI Specification

To export the OpenAPI spec to a file, use the provided script:

```bash
# Start the application first
mvn spring-boot:run

# In another terminal, export the spec
./export-openapi-spec.sh
```

This will create an `openapi.json` file in the project root.

### Custom Export Location

You can customize the output file and API host:

```bash
API_HOST=http://localhost:8080 OUTPUT_FILE=./docs/api-spec.json ./export-openapi-spec.sh
```

## Generating TypeScript Interfaces

Once you have the `openapi.json` file, you can generate TypeScript interfaces for use in your frontend application.

### Using openapi-typescript

```bash
# Install the tool (one-time)
npm install -g openapi-typescript

# Generate TypeScript types
npx openapi-typescript openapi.json -o src/types/api.ts
```

This creates TypeScript interfaces for all:
- Request DTOs (e.g., `CreateQAWRequest`, `OtpVerifyDto`)
- Response DTOs (e.g., `UserDTO`, `QuicklyAddedWordDTO`)
- Enums (e.g., `LanguageName`, `WordType`)

### Using openapi-generator-cli (Full Client)

For a complete TypeScript client with API methods:

```bash
# Install the tool (one-time)
npm install -g @openapitools/openapi-generator-cli

# Generate TypeScript Axios client
npx @openapitools/openapi-generator-cli generate \
  -i openapi.json \
  -g typescript-axios \
  -o ./generated-client \
  --additional-properties=npmName=ord-api-client,npmVersion=1.0.0
```

## CI/CD Integration

### Automated NPM Package Publishing

The project is configured to automatically publish TypeScript types to NPM whenever Kotlin code changes on the `main` branch.

**NPM Package**: [`@ord-api/ord-api-types`](https://www.npmjs.com/package/@ord-api/ord-api-types)

#### How It Works

1. **Trigger**: Workflow runs on push to `main` when Kotlin files or `pom.xml` change
2. **Build**: Application is built and started in background
3. **Export**: OpenAPI spec is exported via `./export-openapi-spec.sh`
4. **Generate**: TypeScript types are generated using `openapi-typescript`
5. **Publish**: Package is published to NPM with auto-incrementing version `1.0.X`
6. **Release**: GitHub release is created with installation instructions

#### Versioning

- Format: `1.0.X` where X is the GitHub Actions run number
- Example versions: `1.0.1`, `1.0.2`, `1.0.3`...
- Each push to main creates a new version automatically

#### Installation

Frontend projects can install the types package:

```bash
# npm
npm install @ord-api/ord-api-types

# pnpm
pnpm add @ord-api/ord-api-types

# yarn
yarn add @ord-api/ord-api-types
```

#### Setup Requirements

To enable NPM publishing, the following secrets must be configured in GitHub:

- `NPM_TOKEN`: NPM automation token (Settings → Secrets → Actions)
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions

#### Workflow File

See `.github/workflows/publish-api-types.yml` for the complete workflow configuration.

#### Package Contents

- `types.ts` - Generated TypeScript type definitions
- `README.md` - Usage documentation with Axios examples
- `package.json` - NPM package metadata

## API Structure

### Documented Endpoints

The following API sections are fully documented:

#### Authentication
- `POST /api/v1/auth/otp-request` - Request OTP code
- `POST /api/v1/auth/otp-verify` - Verify OTP and login
- `DELETE /api/v1/auth/logout` - Logout user

#### Users
- `GET /api/v1/users/me` - Get current user profile
- `POST /api/v1/users/init-account` - Initialize user account

#### Language Proficiencies
- `GET /api/v1/language-proficiencies` - List user's language proficiencies
- `POST /api/v1/language-proficiencies` - Add new language proficiency
- `PATCH /api/v1/language-proficiencies` - Update language proficiency
- `DELETE /api/v1/language-proficiencies/{language}` - Remove language

#### Quickly Added Words (QAW)
- `POST /api/v1/quickly-added-words` - Create a word
- `POST /api/v1/quickly-added-words/bulk-create` - Bulk create words
- `GET /api/v1/quickly-added-words` - List words (paginated)
- `PATCH /api/v1/quickly-added-words/{id}` - Update a word
- `PATCH /api/v1/quickly-added-words/bulk-update` - Bulk update words
- `PATCH /api/v1/quickly-added-words/approve-many` - Approve multiple words
- `DELETE /api/v1/quickly-added-words/{id}` - Delete a word
- `POST /api/v1/quickly-added-words/bulk-delete` - Bulk delete words

#### Health Check
- `GET /api/v1/health-check` - Check application and database health

### Documented DTOs

All major DTOs have been annotated with examples:

- **Auth**: `OtpRequestDto`, `OtpVerifyDto`
- **Users**: `UserDTO`
- **QAW**: `CreateQAWRequest`, `QuicklyAddedWordDTO`
- **Shared**: `PaginatedDataResponse<T>`, `PaginationData`

### Documented Enums

- `LanguageName` - All supported languages
- `WordType` - Types of words/expressions
- `WordExtraMark` - Word classification marks

## Authentication in Swagger UI

To test authenticated endpoints in Swagger UI:

1. First, call `POST /api/v1/auth/otp-request` to send an OTP
2. Call `POST /api/v1/auth/otp-verify` with your email and OTP code
3. The JWT token will be automatically set in cookies
4. All subsequent authenticated requests will work automatically

Alternatively, if you already have a JWT token:

1. Click the "Authorize" button at the top of Swagger UI
2. Enter your JWT token in the format: `Bearer <your-token>`
3. Click "Authorize"
4. All authenticated requests will now include your token

## Configuration

OpenAPI configuration is located in:

- **SpringDoc Settings**: `src/main/resources/application.properties`
- **API Metadata**: `src/main/kotlin/com/ord/config/OpenApiConfig.kt`

### Key Configuration Properties

```properties
# OpenAPI / Swagger Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha
springdoc.show-actuator=false
```

## Next Steps

### Remaining Controllers to Document

The following controllers still need OpenAPI annotations (optional improvements):

- Word CRUD Controller
- Word AI Controller
- Word Details Controller
- Games Controllers (4 variants)
- Conversation Controllers (2)
- Public QAW Controller
- AI Demo Controller

### Additional DTOs to Document

As you add more endpoints, annotate their DTOs with:

```kotlin
@Schema(
    description = "Brief description",
    example = "example value"
)
```

## Troubleshooting

### Swagger UI Not Loading

- Verify the application is running: `curl http://localhost:8080/api/v1/health-check`
- Check that SpringDoc dependency is in `pom.xml`
- Verify application.properties has the correct springdoc settings

### OpenAPI Spec Export Fails

- Ensure the application is fully started before running the export script
- Check that the port 8080 is not blocked by firewall
- Try accessing `http://localhost:8080/v3/api-docs` directly in a browser

### TypeScript Generation Errors

- Ensure your `openapi.json` file is valid JSON
- Try validating it at: https://editor.swagger.io/
- Update to the latest version of openapi-typescript: `npm install -g openapi-typescript@latest`

## Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [openapi-typescript](https://github.com/drwpow/openapi-typescript)
- [OpenAPI Generator](https://openapi-generator.tech/)