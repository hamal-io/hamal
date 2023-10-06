package io.hamal.testbed.admin

import io.hamal.admin.AdminConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.core.config.BackendBasePath
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


@SpringBootTest(
    webEnvironment = DEFINED_PORT,
    properties = [
        "server.port=8053",
        "io.hamal.runner.admin.host=http://localhost:8053",
        "io.hamal.runner.api.host=http://localhost:8053",
        "io.hamal.runner.bridge.host=http://localhost:8053"
    ], classes = [
        AdminTestConfig::class,
        CoreConfig::class,
        AdminConfig::class,
        BridgeConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("admin - sqlite")
@ActiveProfiles(value = ["test", "admin", "sqlite"])
internal class SqliteAdminHamalTest : BaseAdminTest() {
    final override val adminSdk = rootAdminSdk(8053)
    final override val testPath: Path = Paths.get("src", "integrationTest", "resources", "admin")

    @Bean
    fun hubBasePath() = BackendBasePath("/tmp/hamal/test-sqlite/${UUID.randomUUID()}")
}