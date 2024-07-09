package com.backend.ord

import com.backend.ord.utils.Console
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.env.Environment

@SpringBootApplication
open class BackendApplication(private val environment: Environment) : CommandLineRunner {

    override fun run(vararg args: String) {
        Console.printCyan("The main application has started")
        Console.addBreakLine(1)

        verifyEnvironment()
    }

    private fun verifyEnvironment() {
        Console.ensureFunctionSuccess("Verify environment") {
            val testingProperty = environment.getProperty("ENV_TEST_PROPERTY")
            if (testingProperty != "1test1") {
                throw RuntimeException("Environment property verification failed")
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(BackendApplication::class.java, *args)
        }
    }
}
