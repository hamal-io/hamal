package io.hamal.lib.http

import io.hamal.lib.common.util.CollectionUtils.cross
import io.hamal.lib.http.HttpRequest.*
import io.hamal.lib.http.HttpRequest.HttpMethod.*
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
import org.springframework.web.bind.annotation.*

@RestController
open class TestStatusController {
    @RequestMapping("/v1/status")
    fun execute(@RequestParam("code") code: Int): ResponseEntity<String> {
        return HttpStatus.valueOf(code).let {
            ResponseEntity<String>(
                it.toString(),
                it
            )
        }
    }
}

@Nested
@SpringBootTest(
    classes = [TestWebConfig::class, TestStatusController::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = []
)
class StatusIT(
    @LocalServerPort var localServerPort: Int
) {

    @TestFactory
    fun testStatusCode(): List<DynamicTest> =
        HttpStatusCode.values().cross(HttpMethod.values())
            .map { (statusCode, method) ->
                dynamicTest("$method request returns $statusCode") {
                    val testInstance = DefaultHttpClient("http://localhost:$localServerPort")

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/status")
                        Get -> testInstance.get("/v1/status")
                        Patch -> testInstance.patch("/v1/status")
                        Post -> testInstance.post("/v1/status")
                        Put -> testInstance.put("/v1/status")
                    }

                    val response = request.parameter("code", statusCode.value).execute()
                    assertThat(response.statusCode, equalTo(statusCode))
                }
            }
            .toList()
}