package io.hamal.agent.extension.std.debug

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.EnvValue
import io.hamal.lib.script.api.value.IdentValue

class DebugExtension : Extension {
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("debug"),
            values = mapOf(
//                IdentValue("sleep") to Sleep(),
            )
        )
    }

}

//class Sleep : DepFunctionValue {
//    override val ident = IdentValue("sleep")
//    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
//
//    override fun invoke(ctx: Context): Value {
//        val ms = (ctx.parameters.first().value as Number).toLong()
//        val time = measureTimeMillis {
//            Thread.sleep(ms)
//        }
//
//        println(time)
//        return NilValue
//    }
//
//}