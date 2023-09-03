package io.hamal.mono.as_root

import io.hamal.backend.BackendConfig
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        BackendConfig::class,
        RunnerConfig::class,
        TestRunnerConfig::class
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
@DisplayName("as_root - memory")
class MemoryHamalTest : BaseAsRootTest() {
    final override val rootHttpTemplate = HttpTemplate(
        baseUrl = "http://localhost:8042",
        headerFactory = {
            set("x-hamal-token", "test-root-token")
        }
    )
    final override val rootHubSdk = DefaultHubSdk(rootHttpTemplate)
}