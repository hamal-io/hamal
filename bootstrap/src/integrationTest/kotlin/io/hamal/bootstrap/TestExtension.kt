package io.hamal.bootstrap

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value

class TestExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("test"),
            values = mapOf(
                IdentValue("complete") to CompleteTest(),
                IdentValue("fail") to FailTest()
            )
        )
    }

}

class CompleteTest : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        println("Complete Test 123")
        return NilValue
    }
}

class FailTest : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        println("Fail T3ST 123321")
        return NilValue
    }
}