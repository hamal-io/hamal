package io.hamal.script

import io.hamal.script.interpreter.Environment
import io.hamal.script.value.ErrorValue
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


class SandboxIT {
    @TestFactory
    fun generateTestCases(): List<DynamicTest> {
        return collectFiles()
            .map { file ->
                DynamicTest.dynamicTest("${file.parent.name}/${file.name}") {
                    val code = String(Files.readAllBytes(file))
                    val result = testInstance.eval(code)
                    assertTrue(result !is ErrorValue, "$result")
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }

    private val environment = Environment()
    init {
        environment.register(TestForeignLogModule)
    }

    private val testInstance = Sandbox(environment)

    private val testPath = Paths.get("src", "testIntegration", "resources")
}