package io.hamal.agent.extension.std

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*
import kotlin.system.measureTimeMillis

class DebugExtension : Extension {
    override fun create(): DepEnvironmentValue {
        return DepEnvironmentValue(
            identifier = DepIdentifier("debug"),
            values = mapOf(
                DepIdentifier("sleep") to Sleep(),
            )
        )
    }

}

class Sleep : DepFunctionValue {
    override val identifier = DepIdentifier("sleep")
    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")

    override fun invoke(ctx: Context): DepValue {
        val ms = (ctx.parameters.first().value as Number).toLong()
        val time = measureTimeMillis {
            Thread.sleep(ms)
        }

        println(time)
        return DepNilValue
    }

}