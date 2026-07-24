package com.ord.e2e

object E2eWhitelistGuard {
    fun missingFromWhitelist(workerEmails: List<String>, whitelistedEmails: List<String>): List<String> {
        val whitelisted = whitelistedEmails.toSet()
        return workerEmails.filterNot { whitelisted.contains(it) }
    }
}
