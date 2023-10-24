package io.hamal.extension.unsafe.net.http

import AbstractExtensionTest
import io.hamal.extension.safe.std.decimal.DecimalSafeFactory
import io.hamal.extension.unsafe.net.http.web.TestHeaderController
import io.hamal.extension.unsafe.net.http.web.TestJsonController
import io.hamal.extension.unsafe.net.http.web.TestStatusController
import io.hamal.lib.http.fixture.TestWebConfig
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.type.StringType
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
class HttpTest(@LocalServerPort var localServerPort: Int) : AbstractExtensionTest() {

    @TestFactory
    fun run(): List<DynamicTest> {
        NativeLoader.load(Resources)
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.parent.name}/${testFile.parent.name}/${testFile.name}") {
                val config = ExtensionConfig(
                    mutableMapOf(
                        "base_url" to StringType("http://localhost:$localServerPort")
                    )
                )
                val runner = createTestRunner(
                    unsafeFactories = listOf(HttpExtensionFactory(config)),
                    safeFactories = listOf(DecimalSafeFactory)
                )
                runner.run(unitOfWork(String(Files.readAllBytes(testFile))))
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }