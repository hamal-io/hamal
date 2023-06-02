package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepErrorValue
import io.hamal.lib.script.api.value.DepIdentifier
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

                    val env = DepEnvironmentValue(
                        identifier = DepIdentifier("_G"),
                        values = mapOf(
                            AssertFunction.identifier to AssertFunction,
                            RequireFunction.identifier to RequireFunction
                        )
                    )

                    subEnv.addLocal(DepIdentifier("nested-sub-env"), nestedSubEnv)
                    env.addLocal(subEnv.identifier, subEnv)
                    val testInstance = DefaultSandbox(env)

                    val result = testInstance.eval(code)

                    if (result is DepErrorValue) {
                        org.junit.jupiter.api.fail { result.toString() }
                    }
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }


    val subEnv = DepEnvironmentValue(
        identifier = DepIdentifier("sub-env")
    )

    val nestedSubEnv = DepEnvironmentValue(
        identifier = DepIdentifier("nested-sub-env")
    )


    private val testPath = Paths.get("src", "integrationTest", "resources")
}