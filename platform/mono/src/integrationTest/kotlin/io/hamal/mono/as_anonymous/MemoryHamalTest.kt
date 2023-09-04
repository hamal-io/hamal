package io.hamal.mono.as_anonymous

import io.hamal.backend.BackendConfig
import io.hamal.mono.rootHubSdk
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = DEFINED_PORT,
    properties = ["server.port=8042"],
    classes = [
        BackendConfig::class,
        RunnerConfig::class,
        TestAsAnonymousRunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("as_anonymous - memory")
@ActiveProfiles(value = ["test", "memory"])
class MemoryHamalTest : BaseAsAnonymousTest() {
    final override val rootHubSdk = rootHubSdk(8042)
}