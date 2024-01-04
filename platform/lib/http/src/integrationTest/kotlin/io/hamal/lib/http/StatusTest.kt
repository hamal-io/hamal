package io.hamal.lib.http

import io.hamal.lib.common.util.CollectionUtils.cross
import io.hamal.lib.http.HttpRequest.HttpMethod
import io.hamal.lib.http.HttpRequest.HttpMethod.*
import io.hamal.lib.http.fixture.TestWebConfig
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestStatusController {
    @RequestMapping("/v1/status")
    fun execute(
        @RequestParam("code") code: Int,
    ): ResponseEntity<String> {
        val status = HttpStatus.valueOf(code)
        return ResponseEntity<String>(
            status.toString(),
            status
        )
    }
}


@SpringBootTest(classes = [TestWebConfig::class, TestStatusController::class], webEnvironment = RANDOM_PORT)
class StatusTest(@LocalServerPort var localServerPort: Int) {

    @TestFactory
    fun testStatusCode(): List<DynamicTest> =
        HttpStatusCode.values().cross(HttpMethod.values())
            .map { (statusCode, method) ->
                dynamicTest("$method request returns $statusCode") {
                    val testInstance = HttpTemplateImpl("http://localhost:$localServerPort")

                    val request = when (method) {
                        Delete -> testInstance.delete("/v1/status").header("content-type", "application/json")
                        Get -> testInstance.get("/v1/status").header("content-type", "application/json")
                        Patch -> testInstance.patch("/v1/status").header("content-type", "application/json")
                        Post -> testInstance.post("/v1/status").header("content-type", "application/json")
                        Put -> testInstance.put("/v1/status").header("content-type", "application/json")
                    }

                    val response = request.parameter("code", statusCode.value).execute()
                    assertThat(response.statusCode, equalTo(statusCode))
                }
            }
            .toList()
}