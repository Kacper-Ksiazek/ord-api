# Register new test classes into the suite chain

CI runs `mvn -Dtest=com.ord.AllTestsSuite` (see the `Makefile` `test` target and `.github/workflows/deploy.yml`), so a test class only runs if it is reachable from `AllTestsSuite`. Wire new tests up the chain: a feature suite uses `@SelectPackages` to pick up everything in its package, and aggregate suites use `@SelectClasses`. A new controller test placed in `com.ord.controllers.conversations` is picked up by `AllConversationControllersTestsSuite` automatically; a test in a brand-new package must be added to the relevant `@SelectClasses` (and ultimately `AllControllersTestsSuite` / `AllTestsSuite`). A test that is not in the chain silently never runs in CI.

## Good

```kotlin
// Package-scoped suite: any test class added to this package is auto-included.
@Suite
@SuiteDisplayName("  - ( Conversations ): ")
@SelectPackages("com.ord.controllers.conversations")
class AllConversationControllersTestsSuite

// New top-level/standalone classes get added explicitly to the aggregate suite.
@Suite
@SelectClasses(
    value = [
        AllControllersTestsSuite::class,
        RecencyBucketCalculatorTest::class,
        ConversationActivityCalculatorTest::class,
    ]
)
class AllTestsSuite
```

## Bad

```kotlin
// New test class in a fresh package that no suite selects.
package com.ord.controllers.billing
class TestBillingController : ControllerTestBase(/* ... */) { /* green locally */ }
// AllControllersTestsSuite / AllTestsSuite were never updated,
// so this never executes under `mvn -Dtest=com.ord.AllTestsSuite` in CI.
```
