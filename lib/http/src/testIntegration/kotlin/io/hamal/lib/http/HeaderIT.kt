package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.*
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import io.hamal.lib.http.HttpStatusCode.NoContent
import io.hamal.lib.http.fixture.TestWebConfig
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*


@RestController
open class TestHeaderController {
    @RequestMapping("/v1/headers")
    fun execute(
        @RequestHeader headers: MultiValueMap<String, String>
    ): ResponseEntity<Unit> = ResponseEntity(
        headers,
        HttpStatus.NO_CONTENT
    )
}

@Nested
@SpringBootTest(
    classes = [TestWebConfig::class, TestHeaderController::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = []
)
class HeaderIT(
    @LocalServerPort var localServerPort: Int
) {
    @TestFactory
    fun `Header set on template`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {

                    val testInstance = HttpTemplate(
                        baseUrl = "http://localhost:$localServerPort",
                        headerFactory = {
                            this["x-template-header"] = "x-template-header-value"
                        }
                    )

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/headers")
                        Get -> testInstance.get("/v1/headers")
                        Patch -> testInstance.patch("/v1/headers")
                        Post -> testInstance.post("/v1/headers")
                        Put -> testInstance.put("/v1/headers")
                    }

                    val response = request.execute()

                    require(response is NoContentHttpResponse)
                    assertThat(response.statusCode, equalTo(NoContent))

                    val headers = response.headers
                    assertThat(headers["x-template-header"], equalTo("x-template-header-value"))
                }
            }
            .toList()

    @TestFactory
    fun `Header set on request`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {

                    val testInstance = HttpTemplate(
                        baseUrl = "http://localhost:$localServerPort",
                        headerFactory = {
                            this["x-request-header"] = "x-request-header-value"
                        }
                    )

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/headers")
                        Get -> testInstance.get("/v1/headers")
                        Patch -> testInstance.patch("/v1/headers")
                        Post -> testInstance.post("/v1/headers")
                        Put -> testInstance.put("/v1/headers")
                    }

                    val response = request
                        .header("x-request-header", "x-request-header-value")
                        .execute()

                    require(response is NoContentHttpResponse)
                    assertThat(response.statusCode, equalTo(NoContent))

                    val headers = response.headers
                    assertThat(headers["x-request-header"], equalTo("x-request-header-value"))
                }
            }
            .toList()

    @TestFactory
    fun `Header set on request overwrites header set on template`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {

                    val testInstance = HttpTemplate(
                        baseUrl = "http://localhost:$localServerPort",
                        headerFactory = {
                            this["x-header"] = "x-template-value"
                        }
                    )

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/headers")
                        Get -> testInstance.get("/v1/headers")
                        Patch -> testInstance.patch("/v1/headers")
                        Post -> testInstance.post("/v1/headers")
                        Put -> testInstance.put("/v1/headers")
                    }

                    val response = request
                        .header("x-header", "x-request-value")
                        .execute()

                    require(response is NoContentHttpResponse)
                    assertThat(response.statusCode, equalTo(NoContent))

                    val headers = response.headers
                    assertThat(headers["x-header"], equalTo("x-request-value"))
                }
            }
            .toList()

    @TestFactory
    fun `Different header can be set on request and template template at the same time`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {

                    val testInstance = HttpTemplate(
                        baseUrl = "http://localhost:$localServerPort",
                        headerFactory = {
                            this["x-template-header"] = "x-template-value"
                        }
                    )

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/headers")
                        Get -> testInstance.get("/v1/headers")
                        Patch -> testInstance.patch("/v1/headers")
                        Post -> testInstance.post("/v1/headers")
                        Put -> testInstance.put("/v1/headers")
                    }

                    val response = request
                        .header("x-request-header", "x-request-value")
                        .execute()

                    require(response is NoContentHttpResponse)
                    assertThat(response.statusCode, equalTo(NoContent))

                    val headers = response.headers
                    assertThat(headers["x-template-header"], equalTo("x-template-value"))
                    assertThat(headers["x-request-header"], equalTo("x-request-value"))
                }
            }
            .toList()
}