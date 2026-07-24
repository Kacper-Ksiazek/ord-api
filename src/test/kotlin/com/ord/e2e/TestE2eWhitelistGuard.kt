package com.ord.e2e

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("- E2E whitelist guard")
class TestE2eWhitelistGuard {

    @Test
    fun `detects worker emails missing from OTP whitelist`() {
        val workers = listOf("e2e-ci-w0@ord.test", "e2e-ci-w1@ord.test")
        val whitelisted = listOf("e2e-ci-w0@ord.test")

        E2eWhitelistGuard.missingFromWhitelist(workers, whitelisted) shouldBe listOf("e2e-ci-w1@ord.test")
    }

    @Test
    fun `returns empty when all workers are whitelisted`() {
        val workers = listOf("e2e-ci-w0@ord.test", "e2e-ci-w1@ord.test")
        val whitelisted = workers

        E2eWhitelistGuard.missingFromWhitelist(workers, whitelisted) shouldBe emptyList()
    }
}
