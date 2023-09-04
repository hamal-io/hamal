package io.hamal.extension.net.http

import AbstractExtensionTest
import io.hamal.extension.net.http.web.TestStatusRoute
import io.hamal.lib.http.fixture.TestWebConfig
import io.hamal.lib.kua.NativeLoader
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
        TestStatusRoute::class
    ], webEnvironment = RANDOM_PORT
)
class HttpTest(@LocalServerPort var localServerPort: Int) : AbstractExtensionTest() {

    @TestFactory
    fun run(): List<DynamicTest> {
        NativeLoader.load(NativeLoader.Preference.Resources)
        return collectFiles().map { testFile ->
            dynamicTest("${testFile.parent.name}/${testFile.name}") {

                val config = ExtensionConfig(
                    mutableMapOf(
                        "port" to StringType(localServerPort.toString())
                    )
                )

                val execute = createTestExecutor(HttpExtensionFactory(config))
                execute.run(unitOfWork(String(Files.readAllBytes(testFile))))
            }
        }.toList()
    }
}

private val testPath = Paths.get("src", "integrationTest", "resources", "suite")
private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".lua") }