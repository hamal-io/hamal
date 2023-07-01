package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.DefaultFuncInvocationContextFactory
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name


class SandboxTest {
    @TestFactory
    fun generateTestCases(): List<DynamicTest> {
        return collectFiles()
            .map { file ->
                dynamicTest("${file.parent.parent.name}/${file.parent.name}/${file.name}") {
                    val code = String(Files.readAllBytes(file))

                    val env = EnvValue(
                        ident = IdentValue("_G"),
                        values = mapOf(
                            IdentValue("assert") to AssertFunction,
                            IdentValue("require") to RequireFunction
                        )
                    )

                    subEnv.addLocal(IdentValue("nested-sub-env"), nestedSubEnv)
                    env.addLocal(subEnv.ident, subEnv)
                    val testInstance = DefaultSandbox(env, DefaultFuncInvocationContextFactory)

                    val result = testInstance.eval(code)

                    if (result is ErrorValue) {
                        fail { result.toString() }
                    }
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }


    val subEnv = EnvValue(
        ident = IdentValue("sub-env")
    )

    val nestedSubEnv = EnvValue(
        ident = IdentValue("nested-sub-env")
    )


    private val testPath = Paths.get("src", "integrationTest", "resources")
}