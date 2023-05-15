package io.hamal.lib.script.impl.builtin

import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.interpreter.RootEnvironment

internal abstract class AbstractBuiltinTest {

    private val sandbox = DefaultSandbox(RootEnvironment())

    fun eval(code: String): Value {
        return sandbox.eval(code)
    }

}