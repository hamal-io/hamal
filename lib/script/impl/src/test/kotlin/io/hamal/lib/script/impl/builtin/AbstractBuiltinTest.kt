package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.FunctionValue
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.MetaTable
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.eval.RootEnvironment
import org.junit.jupiter.api.fail

internal abstract class AbstractBuiltinTest {

    protected val env = RootEnvironment()
    protected val sandbox: Sandbox

    init {
        env.add(TestEnv)
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

object TestEnv : Environment {

    override val identifier = Identifier("test-env")

    private val values = mutableMapOf<Identifier, Value>(
        Identifier("nested-env") to NestedTestEnv
    )

    override fun addLocal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun addGlobal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun get(identifier: Identifier): Value {
        return requireNotNull(values[identifier])
    }


    override fun findNativeFunction(identifier: Identifier): FunctionValue? {
        TODO("Not yet implemented")
    }

    override fun findEnvironment(identifier: Identifier): Environment? {
        return values[identifier]?.let { it as Environment }
    }

    override val metaTable: MetaTable
        get() = TODO("Not yet implemented")
}

object NestedTestEnv : Environment {
    override val identifier = Identifier("nested-env")

    override fun addLocal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun addGlobal(identifier: Identifier, value: Value) {
        TODO("Not yet implemented")
    }

    override fun get(identifier: Identifier): Value {
        TODO("Not yet implemented")
    }

    override fun findNativeFunction(identifier: Identifier): FunctionValue? {
        TODO("Not yet implemented")
    }

    override fun findEnvironment(identifier: Identifier): Environment? {
        TODO("Not yet implemented")
    }

    override val metaTable: MetaTable
        get() = TODO("Not yet implemented")

}