package io.hamal.mono.api

import io.hamal.api.ApiConfig
import io.hamal.mono.BaseTest
import io.hamal.mono.rootHubSdk
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
    properties = ["server.port=8043", "io.hamal.runner.host=http://localhost:8043"],
    classes = [
        ApiConfig::class,
        RunnerConfig::class
    ]
)
@DirtiesContext
@DisplayName("api - sqlite")
@ExtendWith(SpringExtension::class)
@ActiveProfiles(value = ["test", "sqlite"])
internal class SqliteApiHamalTest : BaseTest() {
    final override val rootHubSdk = rootHubSdk(8043)
    final override val testPath: Path = Paths.get("src", "integrationTest", "resources", "api")
}