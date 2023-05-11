package io.hamal.lib.http

import io.hamal.lib.http.fixture.TestWebConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Serializable
data class TestResponse(val value: String)

@RestController
open class TestParamController {
    @GetMapping("/v1/get")
    fun get(
        @RequestParam("result") resultValue: String
    ): TestResponse {
        return TestResponse(resultValue)
    }
}

@Nested
@SpringBootTest(
    classes = [TestWebConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = []
)

class QueryParamIT(
    @LocalServerPort var localServerPort: Int
) {
    @Test
    fun `Get request`() {

        val client = HttpClientBuilder.create()
            .build()

        val get = HttpGet("http://localhost:${localServerPort}/v1/get?result=some-get-response")
        val response = client.execute(get)
        val r = Json {
            ignoreUnknownKeys = true
        }.decodeFromStream<TestResponse>(response.entity.content)

        println(r)

    }

}