package io.hamal.agent.extension.std.debug

import io.hamal.agent.extension.api.Extension
import io.hamal.agent.extension.api.ExtensionFunc
import io.hamal.agent.extension.api.ExtensionFuncInvocationContext
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.Value
import kotlin.system.measureTimeMillis

class DebugExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("debug"),
            values = mapOf(
                IdentValue("sleep") to Sleep(),
            )
        )
    }

}

class Sleep : ExtensionFunc() {
    override fun invoke(ctx: ExtensionFuncInvocationContext): Value {
        val ms = (ctx.parameters.first() as Number).toLong()
        val time = measureTimeMillis {
            Thread.sleep(ms)
        }

        println(time)
        return NilValue
    }

}