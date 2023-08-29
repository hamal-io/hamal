package io.hamal.extension.net.http

import AbstractExtensionTest
import io.hamal.lib.http.HttpStatusCode.*
import io.hamal.lib.http.fixture.TestWebConfig
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
        return ResponseEntity<String>(status.toString(), status)
    }
}


@SpringBootTest(classes = [TestWebConfig::class, TestStatusController::class], webEnvironment = RANDOM_PORT)
class StatusTest(@LocalServerPort var localServerPort: Int) : AbstractExtensionTest() {

    @TestFactory
    fun `Batch requests`(): List<DynamicTest> =
        listOf(Ok, Created, BadGateway, InternalServerError)
            .map { httpStatusCode ->

                dynamicTest("$httpStatusCode") {
                    val execute = createTestExecutor(HttpExtensionFactory())
                    execute(
                        unitOfWork(
                            """
            local http = require('net.http')
            local err, response = http.execute({
                http.requests.get("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}"),
                http.requests.post("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}"),
                http.requests.patch("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}"),
                http.requests.put("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}"),
                http.requests.delete("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}"),
            })
            
            assert( err == nil )
            assert( response ~= nil)
            assert( #response == 5)

            assert(response[1].statusCode == ${httpStatusCode.value})
            assert(response[2].statusCode == ${httpStatusCode.value})
            assert(response[3].statusCode == ${httpStatusCode.value})
            assert(response[4].statusCode == ${httpStatusCode.value})
            assert(response[5].statusCode == ${httpStatusCode.value})
                    """.trimIndent()
                        )
                    )
                }
            }

    @TestFactory
    fun `Get requests`(): List<DynamicTest> =
        listOf(Ok, Created, BadGateway, InternalServerError)
            .map { httpStatusCode ->

                dynamicTest("$httpStatusCode") {
                    val execute = createTestExecutor(HttpExtensionFactory())
                    execute(
                        unitOfWork(
                            """
            local http = require('net.http')
            local err, response = http.get("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}")
            assert( err == nil )
            assert( response ~= nil)
            
            assert(response.statusCode == ${httpStatusCode.value})
                    """.trimIndent()
                        )
                    )
                }
            }

    @TestFactory
    fun `Post requests`(): List<DynamicTest> =
        listOf(Ok, Created, BadGateway, InternalServerError)
            .map { httpStatusCode ->

                dynamicTest("$httpStatusCode") {
                    val execute = createTestExecutor(HttpExtensionFactory())
                    execute(
                        unitOfWork(
                            """
            local http = require('net.http')
            local err, response = http.post("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}")
            assert( err == nil )
            assert( response ~= nil)
            
            assert(response.statusCode == ${httpStatusCode.value})
                    """.trimIndent()
                        )
                    )
                }
            }

    @TestFactory
    fun `Patch requests`(): List<DynamicTest> =
        listOf(Ok, Created, BadGateway, InternalServerError)
            .map { httpStatusCode ->

                dynamicTest("$httpStatusCode") {
                    val execute = createTestExecutor(HttpExtensionFactory())
                    execute(
                        unitOfWork(
                            """
            local http = require('net.http')
            local err, response = http.patch("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}")
            assert( err == nil )
            assert( response ~= nil)
            
            assert(response.statusCode == ${httpStatusCode.value})
                    """.trimIndent()
                        )
                    )
                }
            }

    @TestFactory
    fun `Put requests`(): List<DynamicTest> =
        listOf(Ok, Created, BadGateway, InternalServerError)
            .map { httpStatusCode ->

                dynamicTest("$httpStatusCode") {
                    val execute = createTestExecutor(HttpExtensionFactory())
                    execute(
                        unitOfWork(
                            """
            local http = require('net.http')
            local err, response = http.put("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}")
            assert( err == nil )
            assert( response ~= nil)
            
            assert(response.statusCode == ${httpStatusCode.value})
                    """.trimIndent()
                        )
                    )
                }
            }

    @TestFactory
    fun `Delete requests`(): List<DynamicTest> =
        listOf(Ok, Created, BadGateway, InternalServerError)
            .map { httpStatusCode ->

                dynamicTest("$httpStatusCode") {
                    val execute = createTestExecutor(HttpExtensionFactory())
                    execute(
                        unitOfWork(
                            """
            local http = require('net.http')
            local err, response = http.delete("http://localhost:$localServerPort/v1/status?code=${httpStatusCode.value}")
            assert( err == nil )
            assert( response ~= nil)
            
            assert(response.statusCode == ${httpStatusCode.value})
                    """.trimIndent()
                        )
                    )
                }
            }
}