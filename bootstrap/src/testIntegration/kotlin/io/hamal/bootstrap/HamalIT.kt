package io.hamal.bootstrap

import io.hamal.agent.AgentConfig
import io.hamal.backend.BackendConfig
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration


@WebAppConfiguration
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [
    BackendConfig::class,
    AgentConfig::class
])

class HamalIT {

    @Test
    fun run() {
        assertTrue(false)
    }

}