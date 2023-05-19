package io.hamal.agent.extension.std.log

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*

class StdLogExtension : Extension {
    override fun create(): EnvironmentValue {
        return EnvironmentValue(
            identifier = Identifier("log"),
            values = mapOf(
                Identifier("config") to TableValue(
                    Identifier("console") to TrueValue,
                    Identifier("backend") to FalseValue
                ),
                Identifier("debug") to LogDebug(),
                Identifier("info") to LogInfo()
            )
        )
    }

}


class LogDebug : FunctionValue {
    override val identifier = Identifier("debug")
    override val metaTable: MetaTable get() = TODO("Not yet implemented")
    override fun invoke(ctx: Context): Value {
        println("DEBUG: ${ctx.parameters.first().value}")
        return NilValue
    }

}


class LogInfo : FunctionValue {
    override val identifier = Identifier("info")
    override val metaTable: MetaTable get() = TODO("Not yet implemented")
    override fun invoke(ctx: Context): Value {
        println("INFO: ${ctx.parameters.first().value}")

//        println("INFO: ${ctx.parameters.first().value}")
        return NilValue
    }

}