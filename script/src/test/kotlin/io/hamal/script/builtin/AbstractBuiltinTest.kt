package io.hamal.script.builtin

import io.hamal.script.Sandbox
import io.hamal.script.value.Value

internal abstract class AbstractBuiltinTest {

    private val sandbox = Sandbox()

    fun eval(code: String): Value {
        return sandbox.eval(code)
    }

}