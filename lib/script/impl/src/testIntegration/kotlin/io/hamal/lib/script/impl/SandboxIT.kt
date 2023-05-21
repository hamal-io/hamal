package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
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
                dynamicTest("${file.parent.parent.name}/${file.parent.name}/${file.name}") {
                    val code = String(Files.readAllBytes(file))

                    val env = EnvironmentValue(
                        identifier = Identifier("_G"),
                        values = mapOf(
                            AssertFunction.identifier to AssertFunction,
                            RequireFunction.identifier to RequireFunction
                        )
                    )

                    subEnv.addLocal(Identifier("nested-sub-env"), nestedSubEnv)
                    env.addLocal(subEnv.identifier, subEnv)
                    val testInstance = DefaultSandbox(env)

                    val result = testInstance.eval(code)

                    if (result is ErrorValue) {
                        org.junit.jupiter.api.fail { result.toString() }
                    }
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }


    val subEnv = EnvironmentValue(
        identifier = Identifier("sub-env")
    )

    val nestedSubEnv = EnvironmentValue(
        identifier = Identifier("nested-sub-env")
    )


    private val testPath = Paths.get("src", "testIntegration", "resources")
}