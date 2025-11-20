package com.ord.shared.annotations

/**
 * Marks an enum class to be automatically exported as a reusable schema in the OpenAPI specification.
 *
 * By default, all enums are NOT exported to prevent potential information leaks.
 * Only enums explicitly annotated with @ExportToOpenAPI will be registered as schemas
 * in the OpenAPI components, making them available as separate union types when generating
 * TypeScript types with tools like openapi-typescript.
 *
 * ## Usage Example:
 * ```kotlin
 * @Schema(description = "Type of word or expression")
 * @ExportToOpenAPI
 * enum class WordType {
 *     NOUN,
 *     VERB,
 *     ADJECTIVE
 * }
 * ```
 *
 * ## Security Note:
 * Only add this annotation to enums that are part of your public API contract.
 * Do NOT annotate internal enums that may contain sensitive information such as:
 * - Internal system states
 * - Permission levels or roles (if sensitive)
 * - Feature flags or internal configuration
 * - Any enum that should remain implementation detail
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ExportToOpenAPI