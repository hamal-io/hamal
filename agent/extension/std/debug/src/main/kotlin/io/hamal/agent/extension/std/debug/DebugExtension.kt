package io.hamal.agent.extension.std.debug

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.*
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

class Sleep : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val ms = (ctx.params.first().value as Number).toLong()
        val time = measureTimeMillis {
            Thread.sleep(ms)
        }

        println(time)
        return NilValue
    }

}