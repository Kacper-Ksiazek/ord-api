package com.ord.e2e

import com.ord.config.properties.OtpProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "e2e.seeder", name = ["enabled"], havingValue = "true")
class E2eDataSeeder(
    private val e2eSeederProperties: E2eSeederProperties,
    private val e2eUserProvisioner: E2eUserProvisioner,
    private val otpProperties: OtpProperties,
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(E2eDataSeeder::class.java)

    override fun run(vararg args: String?) {
        warnIfWorkersNotWhitelisted()

        e2eUserProvisioner.provisionAllWorkers()
            .doOnComplete { log.info("E2E worker accounts provisioned (count={})", e2eSeederProperties.workerCount) }
            .doOnError { error -> log.error("Failed to provision E2E worker accounts", error) }
            .blockLast()
    }

    private fun warnIfWorkersNotWhitelisted() {
        val missing = E2eWhitelistGuard.missingFromWhitelist(
            e2eSeederProperties.workerEmails(),
            otpProperties.getWhitelistedEmailsList(),
        )

        if (missing.isNotEmpty()) {
            log.warn(
                "E2E worker emails are not in OTP whitelist — OTP login will fail for: {}. " +
                    "Set OTP_WHITELISTED_EMAILS to include all worker accounts.",
                missing.joinToString(", "),
            )
        }
    }
}
