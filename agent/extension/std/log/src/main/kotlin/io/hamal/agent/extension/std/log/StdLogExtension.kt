package io.hamal.agent.extension.std.log

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*

object ConsoleFormat : FunctionValue {
    override val identifier: Identifier = Identifier("format")

    override fun invoke(ctx: Context): Value {
        TODO("Not yet implemented")
    }

    override val metaTable: MetaTable
        get() = TODO("Not yet implemented")

}

/**
 * local log = require('log')
 * log._cfg.format = function(values)
 *     return "formatted"
 * end()
 *
 * // or even - invoke another extension and plug it in
 * local log = require('log')
 * local custom = require('custom')
 *      log._cfg.format = custom.formatter
 * end()
 *
 */


class StdLogExtension : Extension {
    override fun create(): EnvironmentValue {
        return EnvironmentValue(
            identifier = Identifier("log"),
            values = mapOf(
                Identifier("_cfg") to TableValue(
                    Identifier("console") to TableValue(
                        Identifier("level") to StringValue("TRACE"),
                        Identifier("format") to ConsoleFormat
                    ),
                    Identifier("backend") to StringValue("INFO")
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