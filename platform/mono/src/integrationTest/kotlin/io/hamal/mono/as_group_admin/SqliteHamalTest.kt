package io.hamal.mono.as_group_admin

import io.hamal.api.ApiConfig
import io.hamal.mono.BaseTest
import io.hamal.mono.rootHubSdk
import io.hamal.runner.RunnerConfig
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Path
import java.nio.file.Paths


@SpringBootTest(
    webEnvironment = DEFINED_PORT,
    properties = ["server.port=8043", "io.hamal.runner.host=http://localhost:8043"],
    classes = [
        ApiConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("as_group_admin - sqlite")
@ActiveProfiles(value = ["test", "sqlite"])
@Disabled
class SqliteHamalTest : BaseTest() {
    final override val rootHubSdk = rootHubSdk(8043)
    final override val testPath: Path = Paths.get("src", "integrationTest", "resources", "as_group_admin")
}