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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Serializable
sealed interface ParameterResponse

@Serializable
data class NoParameterResponse(val result: String = "noContent") : ParameterResponse

@Serializable
data class MultiParameterResponse(val str: String, val num: Int, val boo: Boolean) : ParameterResponse

@RestController
open class TestParameterController {

    @RequestMapping("/v1/parameter")
    fun execute(
        @RequestParam("str", required = false) str: String?,
        @RequestParam("num", required = false) num: Int?,
        @RequestParam("boo", required = false) boo: Boolean?,
        @RequestBody(required = false) parameters: MultiValueMap<String, String>?
    ): ResponseEntity<ParameterResponse> {
        if (str == null && num == null && boo == null && parameters == null) {
            return ResponseEntity.ok(NoParameterResponse(result = "NoContent"))
        }
        if (parameters == null) {
            return ResponseEntity.ok(MultiParameterResponse(str!!, num!!, boo!!))
        }

        return ResponseEntity.ok(
            MultiParameterResponse(
                parameters.getFirst("str")!!,
                parameters.getFirst("num")!!.toInt(),
                parameters.getFirst("boo")!!.toBoolean()
            )
        )
    }
}

@SpringBootTest(classes = [TestWebConfig::class, TestParameterController::class], webEnvironment = RANDOM_PORT)
class ParameterTest(@LocalServerPort var localServerPort: Int) {

    @TestFactory
    fun `No parameters`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {
                    val testInstance = HttpTemplateImpl("http://localhost:$localServerPort")

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/parameter")
                        Get -> testInstance.get("/v1/parameter")
                        Patch -> testInstance.patch("/v1/parameter")
                        Post -> testInstance.post("/v1/parameter")
                        Put -> testInstance.put("/v1/parameter")
                    }

                    val response = request.execute()
                    require(response is HttpSuccessResponse)
                    assertThat(response.statusCode, equalTo(Ok))
                    assertThat(response.result(NoParameterResponse::class), equalTo(NoParameterResponse("NoContent")))
                }
            }
            .toList()

    @TestFactory
    fun `Multiple parameters`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {
                    val testInstance = HttpTemplateImpl("http://localhost:$localServerPort")

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/parameter")
                        Get -> testInstance.get("/v1/parameter")
                        Patch -> testInstance.patch("/v1/parameter")
                        Post -> testInstance.post("/v1/parameter")
                        Put -> testInstance.put("/v1/parameter")
                    }

                    val response = request
                        .parameter("str", "ThatIsAString")
                        .parameter("num", 2810)
                        .parameter("boo", true)
                        .execute()

                    require(response is HttpSuccessResponse)
                    assertThat(response.statusCode, equalTo(Ok))
                    assertThat(
                        response.result(MultiParameterResponse::class), equalTo(
                            MultiParameterResponse(
                                str = "ThatIsAString",
                                num = 2810,
                                boo = true
                            )
                        )
                    )
                }
            }
            .toList()
}