package io.hamal.agent.extension.std.log

//object ConsoleFormat : DepFunctionValue {
//    override val identifier: IdentValue = IdentValue("format")
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


//class StdLogExtension : Extension {
//    override fun create(): EnvValue {
//        return EnvValue(
//            identifier = IdentValue("log"),
//            values = mapOf(
//                IdentValue("_cfg") to TableValue(
//                    IdentValue("console") to TableValue(
//                        IdentValue("level") to StringValue("TRACE"),
//                        IdentValue("format") to ConsoleFormat
//                    ),
//                    IdentValue("backend") to StringValue("INFO")
//                ),
//                IdentValue("debug") to LogDebug(),
//                IdentValue("info") to LogInfo()
//            )
//        )
//    }
//
//}
//
//
//class LogDebug : DepFunctionValue {
//    override val identifier = IdentValue("debug")
//    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
//    override fun invoke(ctx: Context): Value {
//        println("DEBUG: ${ctx.parameters.first().value}")
//        return NilValue
//    }
//
//}
//
//
//class LogInfo : DepFunctionValue {
//    override val identifier = IdentValue("info")
//    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
//    override fun invoke(ctx: Context): Value {
//        val first = ctx.parameters.first().value
//        if (first is IdentValue) {
//            println("INFO: ${ctx.env[first]}")
//        } else {
//            println("INFO: ${first}")
//        }
//
////        println("INFO: ${ctx.parameters.first().value}")
//        return NilValue
//    }
//
//}