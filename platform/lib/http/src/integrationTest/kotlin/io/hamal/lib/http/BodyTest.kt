package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.HttpMethod
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.fixture.TestWebConfig
import kotlinx.serialization.Serializable
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Serializable
sealed interface BodyResponse

@Serializable
data class NoBodyResponse(val result: String = "NoContent") : BodyResponse

@Serializable
data class SomeRequestBodyResponse(val value: Int) : BodyResponse

@Serializable
data class SomeRequestBody(val value: Int)

@RestController
open class TestBodyController {
    @RequestMapping("/v1/body-no-body")
    fun noBody(
        @RequestBody(required = false) body: MultiValueMap<String, String>?
    ): ResponseEntity<BodyResponse> {
        require(body == null)
        return ResponseEntity.ok(NoBodyResponse())
    }

    @RequestMapping("/v1/body-single")
    fun singleBody(
        @RequestBody body: SomeRequestBody
    ): ResponseEntity<BodyResponse> {
        return ResponseEntity.ok(SomeRequestBodyResponse(body.value))
    }
}

@SpringBootTest(classes = [TestWebConfig::class, TestBodyController::class], webEnvironment = RANDOM_PORT)
class BodyTest(@LocalServerPort var localServerPort: Int) {
    @TestFactory
    fun `Request without body`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {
                    val testInstance = HttpTemplateImpl("http://localhost:$localServerPort")

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/body-no-body")
                        Get -> testInstance.get("/v1/body-no-body")
                        Patch -> testInstance.patch("/v1/body-no-body")
                        Post -> testInstance.post("/v1/body-no-body")
                        Put -> testInstance.put("/v1/body-no-body")
                    }

                    val response = request.execute()
                    require(response is SuccessHttpResponse)
                    assertThat(response.statusCode, equalTo(Ok))
                    assertThat(response.result(NoBodyResponse::class), equalTo(NoBodyResponse("NoContent")))
                }
            }
            .toList()

    @TestFactory
    fun `Request with object and JSON serializer`(): List<DynamicTest> =
        listOf(Patch, Post, Put)
            .map { method ->
                dynamicTest("$method request") {
                    val testInstance = HttpTemplateImpl("http://localhost:$localServerPort")

                    val request = when (method) {
                        Patch -> testInstance.patch("/v1/body-single")
                        Post -> testInstance.post("/v1/body-single")
                        Put -> testInstance.put("/v1/body-single")
                        else -> throw IllegalStateException("Only patch post and put are supported")
                    }

                    val response = request
                        .body(SomeRequestBody(value = 1337))
                        .execute()

                    require(response is SuccessHttpResponse)
                    assertThat(response.statusCode, equalTo(Ok))
                    assertThat(response.result(NoBodyResponse::class), equalTo(NoBodyResponse("NoContent")))
                }
            }
            .toList()
}