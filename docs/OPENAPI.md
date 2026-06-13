
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

### Automated Types Package Publishing (GitHub Packages)

The project automatically publishes TypeScript types to **GitHub Packages** whenever the committed `openapi.json` changes on the `main` branch. Everything stays inside the GitHub ecosystem (GitOps) — no external NPM registry, no GitHub releases.

**Package**: `@kacper-ksiazek/ord-api-types` (registry `https://npm.pkg.github.com`)

#### How It Works

1. **Trigger**: Workflow runs on push to `main` when `openapi.json` (the contract) changes
2. **Generate**: TypeScript types are generated from the committed spec using `openapi-typescript`
3. **Publish**: Package is published to GitHub Packages with auto-incrementing version `1.0.X`

The contract is the source of truth: run `make openapi`, commit `openapi.json`, push — that commit triggers the release.

#### Versioning

- Format: `1.0.X` where X is the GitHub Actions run number
- Example versions: `1.0.1`, `1.0.2`, `1.0.3`...
- Each push that changes `openapi.json` creates a new version automatically

#### Installation

GitHub Packages requires authentication even for public packages. Consumers must add an `.npmrc`:

```ini
@kacper-ksiazek:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=${GITHUB_TOKEN}
```

`GITHUB_TOKEN` is a Personal Access Token with the `read:packages` scope (the built-in token in GitHub Actions also works). Then:

```bash
# npm
npm install @kacper-ksiazek/ord-api-types

# pnpm
pnpm add @kacper-ksiazek/ord-api-types

# yarn
yarn add @kacper-ksiazek/ord-api-types
```

#### Setup Requirements

No extra secrets are required. Publishing uses the built-in `GITHUB_TOKEN` with `packages: write` permission (declared in the workflow). The legacy `NPM_TOKEN` secret is no longer used.

#### Workflow File

See `.github/workflows/publish-api-types.yml` for the complete workflow configuration.

#### Package Contents

- `types.ts` - Generated TypeScript type definitions
- `README.md` - Usage documentation with Axios examples
- `package.json` - NPM package metadata

## API Structure

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