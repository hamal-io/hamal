package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultSandbox
import org.junit.jupiter.api.fail

internal abstract class AbstractBuiltinTest {

    protected val env = EnvironmentValue(
        identifier = Identifier("_G"),
        values = mapOf(
            AssertFunction.identifier to AssertFunction,
            RequireFunction.identifier to RequireFunction
        )
    )
    protected val sandbox: Sandbox

    val testEnv = EnvironmentValue(
        identifier = Identifier("test-env")
    )

    val nestedTestEnv = EnvironmentValue(
        identifier = Identifier("nested-env"),
        global = env,
        parent = testEnv
    )

    init {
        testEnv.addLocal(nestedTestEnv.identifier, nestedTestEnv)
        env.addLocal(testEnv.identifier, testEnv)
        sandbox = DefaultSandbox(env)
    }

    fun eval(code: String): Value {
        val result = sandbox.eval(code)
        if (result is ErrorValue) {
            fail { result.cause.toString() }
        }
        return result
    }

    fun expectError(code: String): ErrorValue {
        val result = sandbox.eval(code)
        if (result is ErrorValue) {
            return result
        }
        fail { "An error was expected - but none was thrown" }
    }
}