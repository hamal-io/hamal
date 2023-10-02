package io.hamal.mono.admin

import io.hamal.admin.AdminConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootTest(
    webEnvironment = DEFINED_PORT,
    properties = [
        "server.port=8052",
        "io.hamal.runner.admin.host=http://localhost:8052",
        "io.hamal.runner.api.host=http://localhost:8052",
        "io.hamal.runner.bridge.host=http://localhost:8052"
    ], classes = [
        AdminTestConfig::class,
        CoreConfig::class,
        AdminConfig::class,
        BridgeConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("admin - memory")
@ActiveProfiles(value = ["test", "admin", "memory"])
internal class MemoryAdminHamalTest : BaseAdminTest() {
    final override val adminSdk = rootAdminSdk(8052)
    final override val testPath: Path = Paths.get("src", "integrationTest", "resources", "admin")
}