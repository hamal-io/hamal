package io.hamal.agent.extension.std.log

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.*

//object ConsoleFormat : DepFunctionValue {
//    override val ident: IdentValue = IdentValue("format")
//
//    override fun invoke(ctx: Context): Value {
//        TODO("Not yet implemented")
//    }
//
//    override val metaTable: DepMetaTable
//        get() = TODO("Not yet implemented")
//
//}

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
    override fun create(): EnvValue {
        return EnvValue(
            ident = IdentValue("log"),
            values = TableValue(
                "_cfg" to TableValue(
                    "console" to TableValue(
                        "level" to StringValue("TRACE"),
                    ),
                    "backend" to StringValue("INFO")
                ),
                IdentValue("debug") to LogDebug(),
                IdentValue("info") to LogInfo()
            )
        )
    }

}


class LogDebug : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        println("DEBUG: ${ctx.params.first()}")
        return NilValue
    }

}


class LogInfo : FuncValue() {
    override fun invoke(ctx: FuncContext): Value {
        val first = ctx.params.first().value
        if (first is IdentValue) {
            println("INFO: ${ctx.env[first]}")
        } else {
            println("INFO: ${first}")
        }


//        println("INFO: ${ctx.parameters.first().value}")
        return NilValue
    }

}