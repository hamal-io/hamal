package io.hamal.mono.as_group_admin

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
        TestAsGroupAdminRunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("as_group_admin - memory")
@ActiveProfiles(value = ["test", "memory"])
class MemoryHamalTest : BaseAsGroupAdminTest() {
    final override val rootHubSdk = rootHubSdk(8042)
}