package com.ord.e2e

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "e2e.seeder")
class E2eSeederProperties(
    var enabled: Boolean = false,
    var workerCount: Int = 4,
    var emailPrefix: String = "e2e-ci-w",
    var emailDomain: String = "ord.test",
) {
    fun workerEmails(): List<String> =
        (0 until workerCount).map { index -> "$emailPrefix$index@$emailDomain" }
}
