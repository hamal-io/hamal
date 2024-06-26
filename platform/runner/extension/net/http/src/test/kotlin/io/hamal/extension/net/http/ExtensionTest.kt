package io.hamal.extension.net.http

import io.hamal.extension.net.http.endpoint.TestHeaderController
import io.hamal.extension.net.http.endpoint.TestJsonController
import io.hamal.extension.net.http.endpoint.TestStatusController
import io.hamal.extension.std.decimal.ExtensionStdDecimalFactory
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.RunnerEnv
import io.hamal.plugin.net.http.PluginHttpFactory
import io.hamal.plugin.net.http.fixture.TestWebConfig
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


@SpringBootTest(
    classes = [
        TestWebConfig::class,
        TestHeaderController::class,
        TestJsonController::class,
        TestStatusController::class
    ], webEnvironment = RANDOM_PORT
)
class ExtensionHttpTest(@LocalServerPort var localServerPort: Int) {

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}") {
                createTestRunner(
                    pluginFactories = listOf(PluginHttpFactory()),
                    extensionFactories = listOf(
                        ExtensionStdDecimalFactory,
                        ExtensionNetHttpFactory
                    ),
                    env = RunnerEnv(
                        ValueObject.builder()
                            .set("test_url", "http://localhost:$localServerPort")
                            .build()
                    )
                ).also { runner ->
                    runner.run(unitOfWork(String(Files.readAllBytes(testFile))))
                }
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "test", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }