package io.hamal.lib.http

import io.hamal.lib.common.util.CollectionUtils.cross
import io.hamal.lib.http.HttpRequest.HttpMethod.Get
import io.hamal.lib.http.fixture.TestWebConfig
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestStatusController {
    @GetMapping("/v1/status")
    fun get(
        @RequestParam("status") statusCode: Int
    ): ResponseEntity<String> {
        val resultStatus = HttpStatus.valueOf(statusCode)
        return ResponseEntity<String>(
            resultStatus.toString(),
            resultStatus
        )
    }
}

@Nested
@SpringBootTest(
    classes = [TestWebConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = []
)
class StatusIT(
    @LocalServerPort var localServerPort: Int
) {

    @TestFactory
    fun testStatusCode(): List<DynamicTest> =
        HttpStatusCode.values().cross(HttpRequest.HttpMethod.values())
            .map { (statusCode, method) ->
                DynamicTest.dynamicTest("$method request returns $statusCode") {
                    val testInstance = DefaultHttpClient("http://localhost:$localServerPort")

                    val request = when (method) {
                        Get -> testInstance.get("/v1/get")
                        else -> TODO()
                    }

                    val response = request.execute()
                    assertThat(response.statusCode, equalTo(statusCode))
                }
            }
            .toList()
}