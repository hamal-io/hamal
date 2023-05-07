package io.hamal.script.impl.builtin

import io.hamal.script.api.value.Value
import io.hamal.script.impl.SandboxImpl
import io.hamal.script.impl.interpreter.Environment

internal abstract class AbstractBuiltinTest {

    private val sandbox = SandboxImpl(Environment())

    fun eval(code: String): Value {
        return sandbox.eval(code)
    }

}