package io.hamal.testbed.authorized

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
        "server.port=8053",
        "io.hamal.runner.api.host=http://localhost:8053",
        "io.hamal.runner.bridge.host=http://localhost:8053"
    ], classes = [
        ApiTestConfig::class,
        CoreConfig::class,
        ApiConfig::class,
        BridgeConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("authorized - sqlite")
@ActiveProfiles(value = ["test", "authorized", "sqlite"])
internal class SqliteAuthorizedTest : BaseTest() {
    override val log: Logger = logger(this::class)
    override val sdk = withApiSdk(8053)
    override val testPath: Path = Paths.get("src", "integrationTest", "resources", "authorized")
}