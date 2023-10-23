package io.hamal.lib.http

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class HttpStatusCodeTest {
    @TestFactory
    fun of(): List<DynamicTest> = listOf(
        "Ok" to 200,
        "Created" to 201,
        "Accepted" to 202,
        "NoContent" to 204,
        "BadRequest" to 400,
        "Unauthorized" to 401,
        "PaymentRequired" to 402,
        "Forbidden" to 403,
        "NotFound" to 404,
        "MethodNotAllowed" to 405,
        "NotAcceptable" to 406,
        "Conflict" to 409,
        "PayloadToLarge" to 413,
        "UnsupportedMediaType" to 415,
        "TooManyRequests" to 429,
        "InternalServerError" to 500,
        "NotImplemented" to 501,
        "BadGateway" to 502,
        "Unavailable" to 503,
        "Timeout" to 504
    ).map { (name, code) ->
        dynamicTest("$code - $name") {
            val result = HttpStatusCode.of(code)
            assertThat(result.name, equalTo(name))
            assertThat(result.value, equalTo(code))
        }
    }

    @Test
    fun `HttpStatusCode has 20 elements`() {
        assertThat(HttpStatusCode.values().toList(), hasSize(20))
    }
}