package com.ord

import com.ord.controllers.AllControllersTestsSuite
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
    ]
)
class AllTestsSuite