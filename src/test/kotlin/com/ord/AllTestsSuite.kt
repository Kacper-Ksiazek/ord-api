package com.ord

import com.ord.controllers.AllControllersTestsSuite
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName


@Suite
@SuiteDisplayName("ORD API - All Tests")
@SelectClasses(AllControllersTestsSuite::class)
class AllTestsSuite