# Structure tests with @Nested Positive/Negative and @DisplayName

Group tests per endpoint in an `inner class` annotated with `@DisplayName("[METHOD] /path - description")`, then split into `@Nested` `Positive` and `Negative` inner classes (add focused groups like `Filtering` when useful). Name test functions with backticked sentences that start with the expected HTTP status (e.g. `` `200 - ...` ``, `` `404 - ...` ``). This keeps the test report readable and forces explicit coverage of both happy and failure paths.

## Good

```kotlin
@Nested
@DisplayName("[GET] /api/v1/conversations/{conversationId} - get conversation by ID")
inner class GetConversationByIdTests {

    @Nested
    @DisplayName("Positive")
    inner class Positive {
        @Test
        fun `200 - should return conversation by ID`() { /* ... */ }
    }

    @Nested
    @DisplayName("Negative")
    inner class Negative {
        @Test
        fun `401 - anonymous user cannot get conversation by ID`() { /* ... */ }

        @Test
        fun `404 - should return 404 for non-existent conversation`() { /* ... */ }
    }
}
```

## Bad

```kotlin
// Flat, unlabeled tests with no positive/negative split and vague names.
@Test
fun testGetConversation() { /* only the happy path, no 401/404 coverage */ }

@Test
fun test2() { /* what does this even verify? */ }
```
