package io.hamal.mono

import io.hamal.backend.instance.BackendConfig
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.mono.config.TestRunnerConfig
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        TestRunnerConfig::class,
        BackendConfig::class,
        RunnerConfig::class
    ]
)
@SpringBootTest(
    webEnvironment = DEFINED_PORT,
    properties = [
        "server.port=8042"
    ]
)
@ActiveProfiles(value = ["test", "memory"])
@DirtiesContext
class MemoryHamalTest : BaseHamalTest() {
    final override val httpTemplate = HttpTemplate("http://localhost:8042")
    final override val sdk = DefaultHamalSdk(httpTemplate)

    @Bean
    fun httpTemplateSupplier(): HttpTemplateSupplier = { HttpTemplate("http://localhost:8042") }
}