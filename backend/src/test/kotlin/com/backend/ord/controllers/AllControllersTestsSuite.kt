package com.backend.ord.controllers

import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName

@Suite
@SelectPackages("com.backend.ord.controllers")
@SuiteDisplayName("- Controllers: ")
class AllControllersTestsSuite