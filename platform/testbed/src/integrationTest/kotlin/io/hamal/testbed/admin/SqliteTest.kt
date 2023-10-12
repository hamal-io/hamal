package io.hamal.testbed.admin

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.lib.common.Logger
import io.hamal.lib.common.logger
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
        "server.port=8043",
        "io.hamal.runner.api.host=http://localhost:8043",
        "io.hamal.runner.bridge.host=http://localhost:8043",
        "io.hamal.runner.http.poll-every-ms=10"
    ], classes = [
        ApiTestConfig::class,
        CoreConfig::class,
        ApiConfig::class,
        BridgeConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("admin - sqlite")
@ActiveProfiles(value = ["test", "admin", "sqlite"])
internal class SqliteAdminTest : BaseTest() {
    final override val log: Logger = logger(this::class)
    final override val sdk = withApiSdk(8043)
    final override val testPath: Path = Paths.get("src", "integrationTest", "resources", "admin")
}