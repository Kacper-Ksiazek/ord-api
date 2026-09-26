package com.ord.controllers

import com.ord.controllers.ai_explainer.TestAIExplainerController
import com.ord.controllers.auth.TestAuthController
import com.ord.controllers.auth.TestAuthCookieAttributes
import com.ord.controllers.banks.TestBankController
import com.ord.controllers.conversations.AllConversationControllersTestsSuite
import com.ord.controllers.games.AllGameControllersTestsSuite
import com.ord.controllers.health.TestHealthCheckController
import com.ord.controllers.language_proficiencies.TestLanguageProficienciesController
import com.ord.controllers.tts.TestTtsController
import com.ord.controllers.users.TestUsersController
import com.ord.controllers.words.AllWordControllersTestsSuite
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SuiteDisplayName("- Controllers: ")
@SelectClasses(
    value = [
        TestAuthController::class,
        TestAuthCookieAttributes::class,
        TestBankController::class,
        TestUsersController::class,
        TestLanguageProficienciesController::class,
        AllWordControllersTestsSuite::class,
        AllGameControllersTestsSuite::class,
        AllConversationControllersTestsSuite::class,
        TestAIExplainerController::class,
        TestTtsController::class,
        TestHealthCheckController::class,
    ]
)
class AllControllersTestsSuite
