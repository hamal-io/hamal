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
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*

@RestController
open class TestStatusController {
    @RequestMapping("/v1/status")
    fun execute(
        @RequestParam("code") code: Int?,
        @RequestBody(required = false) body: MultiValueMap<String, String>?
    ): ResponseEntity<String> {
        val status = if (code != null) {
            HttpStatus.valueOf(code)
        } else {
            HttpStatus.valueOf(body!!.getFirst("code")!!.toInt())
        }
        return ResponseEntity<String>(
            status.toString(),
            status
        )
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
                    val testInstance = HttpTemplate("http://localhost:$localServerPort")

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