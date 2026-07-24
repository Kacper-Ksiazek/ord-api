package com.ord

import com.ord.controllers.AllControllersTestsSuite
import com.ord.e2e.TestE2eUserProvisioner
import com.ord.e2e.TestE2eWhitelistGuard
import com.ord.e2e.TestE2eWorkerFlywaySeed
import com.ord.features.conversation.models.conversation.RecencyBucketCalculatorTest
import com.ord.features.conversation.models.conversation_activity.ConversationActivityCalculatorTest
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName


@Suite
@SuiteDisplayName("ORD API - All Tests")
@SelectClasses(
    value = [
        AllControllersTestsSuite::class,
        RecencyBucketCalculatorTest::class,
        ConversationActivityCalculatorTest::class,
        TestE2eWorkerFlywaySeed::class,
        TestE2eUserProvisioner::class,
        TestE2eWhitelistGuard::class,
    ]
)
class AllTestsSuite