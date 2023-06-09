package io.hamal.lib.script.impl.builtin

import io.hamal.lib.common.value.EnvValue
import io.hamal.lib.common.value.ErrorValue
import io.hamal.lib.common.value.IdentValue
import io.hamal.lib.common.value.Value
import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.impl.DefaultSandbox
import org.junit.jupiter.api.fail

internal abstract class AbstractBuiltinTest {

    protected val env = EnvValue(
        ident = IdentValue("_G"),
        values = mapOf(
            TODO()
//            AssertFunction.identifier to AssertFunction,
//            RequireFunction.identifier to RequireFunction
        )
    )
    protected val sandbox: Sandbox

    val testEnv = EnvValue(
        ident = IdentValue("test-env")
    )

    val nestedTestEnv = EnvValue(
        ident = IdentValue("nested-env"),
        global = env,
        parent = testEnv
    )

    init {
        testEnv.addLocal(nestedTestEnv.ident, nestedTestEnv)
        env.addLocal(testEnv.ident, testEnv)
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