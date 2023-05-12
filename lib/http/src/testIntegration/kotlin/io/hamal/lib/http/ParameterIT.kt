package io.hamal.lib.http

import io.hamal.lib.http.HttpRequest.*
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import io.hamal.lib.http.HttpStatusCode.Ok
import io.hamal.lib.http.fixture.TestWebConfig
import kotlinx.serialization.Serializable
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    ): ResponseEntity<ParameterResponse> {
        if (str == null && num == null && boo == null) {
            return ResponseEntity.ok(NoParameterResponse(result = "NoContent"))
        }
        return ResponseEntity.ok(MultiParameterResponse(str!!, num!!, boo!!))
    }
}

@Nested
@SpringBootTest(
    classes = [TestWebConfig::class, TestParameterController::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = []
)
class ParameterIT(
    @LocalServerPort var localServerPort: Int
) {

    @TestFactory
    fun `No parameters`(): List<DynamicTest> =
        HttpMethod.values()
            .map { method ->
                dynamicTest("$method request") {
                    val testInstance = DefaultHttpClient("http://localhost:$localServerPort")

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/parameter")
                        Get -> testInstance.get("/v1/parameter")
                        Patch -> testInstance.patch("/v1/parameter")
                        Post -> testInstance.post("/v1/parameter")
                        Put -> testInstance.put("/v1/parameter")
                    }

                    val response = request.execute()
                    require(response is SuccessHttpResponse)
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
                    val testInstance = DefaultHttpClient("http://localhost:$localServerPort")

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

                    require(response is SuccessHttpResponse)
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