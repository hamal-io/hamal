package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.MetaTable
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.eval.RootEnvironment
import org.junit.jupiter.api.fail

internal abstract class AbstractBuiltinTest {

    private val rootEnv = RootEnvironment()
    private val sandbox: Sandbox

    init {
        rootEnv.add(TestEnv)
        sandbox = DefaultSandbox(rootEnv)
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

internal object TestEnv : Environment {
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