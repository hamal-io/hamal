package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.SandboxImpl
import io.hamal.lib.script.impl.interpreter.Environment

internal abstract class AbstractBuiltinTest {

    private val sandbox = io.hamal.lib.script.impl.SandboxImpl(Environment())

    fun eval(code: String): Value {
        return sandbox.eval(code)
    }

}