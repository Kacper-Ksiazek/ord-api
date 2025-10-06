package com.ord

import com.ord.shared.utils.Console
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.env.Environment
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.net.URI

@SpringBootApplication
@EnableTransactionManagement
class Application(
    private val environment: Environment,
    private val dbClient: DatabaseClient,
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
            dbClient.sql("SELECT 1")
                .fetch()
                .first()
                .block() ?: throw RuntimeException("Database connection verification failed")
        }
    }
}

fun convertHerokuDatabaseUrl() {
    val databaseUrl = System.getenv("DATABASE_URL")
        ?: throw IllegalStateException("❌ Missing environment variable DATABASE_URL. Please make sure it is set!")

    try {
        val uri = URI(databaseUrl.replace("postgres://", "postgresql://"))
        val (username, password) = uri.userInfo.split(":")

        val jdbcUrl = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"
        val r2dbcUrl = "r2dbc:postgresql://${uri.host}:${uri.port}${uri.path}"


        System.setProperty("DATABASE_URL_JDBC", jdbcUrl)     // For Flyway migrations
        System.setProperty("DATABASE_URL_R2DBC", r2dbcUrl)   // For Application running on WebFlux with R2DBC
        System.setProperty("DATABASE_USERNAME", username)
        System.setProperty("DATABASE_PASSWORD", password)

    } catch (e: Exception) {
        throw IllegalStateException("❌ Failed to parse DATABASE_URL: $databaseUrl", e)
    }
}

fun main(args: Array<String>) {
    convertHerokuDatabaseUrl()

    SpringApplication.run(Application::class.java, *args)
}

