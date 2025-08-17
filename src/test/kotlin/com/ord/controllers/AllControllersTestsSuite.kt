package com.ord.controllers

import com.ord.controllers.conversations.AllConversationControllersTestsSuite
import com.ord.controllers.games.AllGameControllersTestsSuite
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("- Controllers: ")
@SelectClasses(
    value = [
        TestAuthController::class,
        TestWordsController::class,
        AllGameControllersTestsSuite::class,
        AllConversationControllersTestsSuite::class
    ]
)
class AllControllersTestsSuite