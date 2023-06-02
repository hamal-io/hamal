package io.hamal.bootstrap

import io.hamal.agent.AgentConfig
import io.hamal.backend.BackendConfig
import io.hamal.lib.http.HttpTemplate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.*
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        BackendConfig::class,
        AgentConfig::class
    ]
)
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    properties = ["server.port=8084"]
)
class HamalIT(
    @LocalServerPort val localPort: Int
) {

    @Test
    fun run() {
        println("RUNS ON: $localPort")

        repeat(10) {
            HttpTemplate("http://localhost:8084")
                .post("/v1/adhoc")
                .body(
                    "text/plain",
                    """
                    local log = require('log')
                    log.info('automate the world')
            """.trimIndent().toByteArray()
                )
                .execute()
        }

        Thread.sleep(1000)
    }
}