package io.hamal.plugin.net.http

import AbstractRunnerTest
import io.hamal.lib.http.fixture.TestWebConfig
import io.hamal.lib.kua.extend.ExtensionConfig
import io.hamal.lib.kua.type.StringType
import io.hamal.plugin.net.http.endpoint.TestHeaderController
import io.hamal.plugin.net.http.endpoint.TestJsonController
import io.hamal.plugin.net.http.endpoint.TestStatusController
import io.hamal.extension.std.decimal.DecimalScriptFactory
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
class HttpTest(@LocalServerPort var localServerPort: Int) : AbstractRunnerTest() {

    @TestFactory
    fun run(): List<DynamicTest> {
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}") {
                val config = ExtensionConfig(
                    mutableMapOf(
                        "base_url" to StringType("http://localhost:$localServerPort")
                    )
                )
                val runner = createTestRunner(
                    pluginFactories = listOf(HttpPluginFactory(config)),
                    scriptFactories = listOf(DecimalScriptFactory)
                )
                runner.run(unitOfWork(String(Files.readAllBytes(testFile))))
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }