package com.ord.openapi

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.controllers.bases.TestcontainersConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.file.Path
import kotlin.io.path.readText

@DisplayName("- OpenAPI contract")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestOpenApiSpec @Autowired constructor(
    private val webClient: WebTestClient,
) : TestcontainersConfig() {

    private val objectMapper = ObjectMapper()
    private val swaggerUsername = System.getenv("SWAGGER_USERNAME") ?: "admin"
    private val swaggerPassword = System.getenv("SWAGGER_PASSWORD") ?: "admin"

    @Test
    fun `committed openapi json matches live spec`() {
        val liveSpec = fetchLiveOpenApiSpec()
        val committedSpec = readCommittedOpenApiSpec()

        if (liveSpec != committedSpec) {
            throw AssertionError(
                """
                openapi.json is out of date.

                Re-export the spec from a running app and commit the result:
                  make openapi
                  git diff openapi.json
                """.trimIndent()
            )
        }
    }

    private fun fetchLiveOpenApiSpec(): JsonNode =
        webClient
            .get()
            .uri("/v3/api-docs")
            .headers { headers -> headers.setBasicAuth(swaggerUsername, swaggerPassword) }
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .returnResult()
            .responseBody
            ?.let(objectMapper::readTree)
            ?: error("Failed to fetch live OpenAPI spec from /v3/api-docs")

    private fun readCommittedOpenApiSpec(): JsonNode {
        val specPath = Path.of("openapi.json")
        specPath.toFile().exists() shouldBe true
        return objectMapper.readTree(specPath.readText())
    }
}
