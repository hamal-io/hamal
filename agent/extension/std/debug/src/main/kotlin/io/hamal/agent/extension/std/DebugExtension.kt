package io.hamal.agent.extension.std

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*
import kotlin.system.measureTimeMillis

class DebugExtension : Extension {
    override fun create(): EnvironmentValue {
        return EnvironmentValue(
            identifier = Identifier("debug"),
            values = mapOf(
                Identifier("sleep") to ExecFunc(),
            )
        )
    }

}

class ExecFunc : FunctionValue {
    override val identifier = Identifier("sleep")
    override val metaTable: MetaTable get() = TODO("Not yet implemented")

    override fun invoke(ctx: Context): Value {
        val ms = (ctx.parameters.first().value as Number).toLong()
        val time = measureTimeMillis {
            Thread.sleep(ms)
        }

        println(time)
        return NilValue
    }

}