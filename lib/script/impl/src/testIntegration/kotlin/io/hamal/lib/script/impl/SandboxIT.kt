package io.hamal.lib.script.impl

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.MetaTable
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.eval.RootEnvironment
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
                    if (result is ErrorValue) {
                        org.junit.jupiter.api.fail { result.toString() }
                    }
                }
            }
            .toList()
    }

    private fun collectFiles() = Files.walk(testPath).filter { f: Path -> f.name.endsWith(".hs") }

    private val env = RootEnvironment()
    private val testInstance: Sandbox

    init {
        env.add(TestEnv)
        testInstance = DefaultSandbox(env)
    }

    private val testPath = Paths.get("src", "testIntegration", "resources")

    object TestEnv : Environment {
        override val identifier = Identifier("test-env")

        override fun addLocal(identifier: Identifier, value: Value) {
            TODO("Not yet implemented")
        }

        override fun addGlobal(identifier: Identifier, value: Value) {
            TODO("Not yet implemented")
        }

        override fun get(identifier: Identifier): Value {
            TODO("Not yet implemented")
        }

        override fun get(identifier: String): Value {
            TODO("Not yet implemented")
        }

        override fun findNativeFunction(identifier: Identifier): NativeFunction? {
            TODO("Not yet implemented")
        }

        override fun findEnvironment(identifier: Identifier): Environment? {
            TODO("Not yet implemented")
        }

        override val metaTable: MetaTable
            get() = TODO("Not yet implemented")

    }
}