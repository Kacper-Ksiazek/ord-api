package com.ord.controllers

import com.ord.controllers.ai_explainer.TestAIExplainerController
import com.ord.controllers.auth.TestAuthController
import com.ord.controllers.conversations.AllConversationControllersTestsSuite
import com.ord.controllers.games.AllGameControllersTestsSuite
import com.ord.controllers.language_proficiencies.TestLanguageProficienciesController
import com.ord.controllers.quickly_added_words.AllQAWControllersTestsSuite
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
        TestUsersController::class,
        TestLanguageProficienciesController::class,
        AllWordControllersTestsSuite::class,
        AllGameControllersTestsSuite::class,
        AllConversationControllersTestsSuite::class,
        AllQAWControllersTestsSuite::class,
        TestAIExplainerController::class,
        TestTtsController::class,
    ]
)
class AllControllersTestsSuite