package com.backend.ord

import com.backend.ord.utils.Console
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class BackendApplication(
    private val environment: Environment,
    private val jdbcTemplate: JdbcTemplate
) : CommandLineRunner {

    override fun run(vararg args: String) {
        Console.printCyan("\nThe main application is booting up...")
        Console.addBreakLine(1)

        verifyEnvironment()
        verifyDatabaseConnection()

        Console.addBreakLine(1)
        Console.printGreen("The main application has started successfully!")
        Console.addBreakLine(3)
    }

    private fun verifyEnvironment() {
        Console.ensureFunctionSuccess("1. Verify environment") {
            val testingProperty = environment.getProperty("ENV_TEST_PROPERTY")
            if (testingProperty != "1test1") {
                throw RuntimeException("Environment property verification failed")
            }
        }
    }

    private fun verifyDatabaseConnection() {
        Console.ensureFunctionSuccess("2. Verify database connection") {
            jdbcTemplate.execute("SELECT 1")
        }
    }
}

fun main(args: Array<String>) {
    println("test")
    SpringApplication.run(BackendApplication::class.java, *args)
}

