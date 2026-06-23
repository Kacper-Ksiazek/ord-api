# Use Kotest matchers for assertions

Assert with Kotest matchers (`shouldBe`, `shouldNotBe`, `shouldHaveSize`, `shouldBeEmpty`, `shouldNotBeNull`, `shouldNotBeBlank`, `shouldBeGreaterThan`, ...), not JUnit `assertEquals`/`assertTrue`. They read as natural-language infix expressions and `shouldNotBeNull()` smart-casts the value so you can keep chaining assertions on it. Import the specific matcher from its `io.kotest.matchers.*` package.

## Good

```kotlin
response.status shouldBe HttpStatus.OK
response.body shouldNotBe null
response.body!!.shouldBeEmpty()

response.body!! shouldHaveSize 1
response.body[0].topic shouldBe TestData.TOPIC

response.body.shouldNotBeNull()
response.body.shouldNotBeBlank()
```

## Bad

```kotlin
// JUnit assertions: inconsistent with the rest of the suite and no smart-cast.
assertEquals(HttpStatus.OK, response.status)
assertNotNull(response.body)
assertEquals(1, response.body!!.size)
assertTrue(response.body[0].topic == TestData.TOPIC)
```
