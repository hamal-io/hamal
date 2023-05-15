package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.impl.interpreter.RootEnvironment
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
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
                dynamicTest("${file.parent.name}/${file.name}") {
                    val code = String(Files.readAllBytes(file))
                    val result = testInstance.eval(code)
                    assertTrue(result !is ErrorValue, "$result")
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }

    private val env = RootEnvironment()

    private val testInstance = DefaultSandbox(env)

    private val testPath = Paths.get("src", "testIntegration", "resources")
}