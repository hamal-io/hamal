package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.DepEnvironmentValue
import io.hamal.lib.script.api.value.DepErrorValue
import io.hamal.lib.script.api.value.DepIdentifier
import io.hamal.lib.script.api.value.DepValue
import io.hamal.lib.script.impl.DefaultSandbox
import org.junit.jupiter.api.fail

internal abstract class AbstractBuiltinTest {

    protected val env = DepEnvironmentValue(
        identifier = DepIdentifier("_G"),
        values = mapOf(
            AssertFunction.identifier to AssertFunction,
            RequireFunction.identifier to RequireFunction
        )
    )
    protected val sandbox: Sandbox

    val testEnv = DepEnvironmentValue(
        identifier = DepIdentifier("test-env")
    )

    val nestedTestEnv = DepEnvironmentValue(
        identifier = DepIdentifier("nested-env"),
        global = env,
        parent = testEnv
    )

    init {
        testEnv.addLocal(nestedTestEnv.identifier, nestedTestEnv)
        env.addLocal(testEnv.identifier, testEnv)
        sandbox = DefaultSandbox(env)
    }

    fun eval(code: String): DepValue {
        val result = sandbox.eval(code)
        if (result is DepErrorValue) {
            fail { result.cause.toString() }
        }
        return result
    }

    fun expectError(code: String): DepErrorValue {
        val result = sandbox.eval(code)
        if (result is DepErrorValue) {
            return result
        }
        fail { "An error was expected - but none was thrown" }
    }
}