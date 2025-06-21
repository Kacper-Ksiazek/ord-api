package com.ord.controllers

import com.ord.controllers.games.AllGameControllersTests
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("- Controllers: ")
@SelectClasses(
    value = [
        TestAuthController::class,
        TestWordsController::class,
        AllGameControllersTests::class
    ]
)
class AllControllersTestsSuite