package io.hamal.mono.admin

import io.hamal.admin.AdminConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootTest(
    webEnvironment = DEFINED_PORT,
    properties = [
        "server.port=8042",
        "io.hamal.runner.admin.host=http://localhost:8042",
        "io.hamal.runner.api.host=http://localhost:8042",
        "io.hamal.runner.bridge.host=http://localhost:8042"
    ], classes = [
        AdminConfig::class,
        CoreConfig::class,
        BridgeConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("admin - memory")
@ExtendWith(SpringExtension::class)
@ActiveProfiles(value = ["test", "memory"])
internal class MemoryAdminHamalTest : BaseAdminTest() {
    final override val adminSdk = rootAdminSdk(8042)
    final override val testPath: Path = Paths.get("src", "integrationTest", "resources", "admin")
}