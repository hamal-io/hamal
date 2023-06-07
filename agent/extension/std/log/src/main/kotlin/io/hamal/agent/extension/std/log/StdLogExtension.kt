package io.hamal.agent.extension.std.log

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Context
import io.hamal.lib.script.api.value.*

object ConsoleFormat : DepFunctionValue {
    override val identifier: DepIdentifier = DepIdentifier("format")

    override fun invoke(ctx: Context): DepValue {
        TODO("Not yet implemented")
    }

    override val metaTable: DepMetaTable
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
    override fun create(): DepEnvironmentValue {
        return DepEnvironmentValue(
            identifier = DepIdentifier("log"),
            values = mapOf(
                DepIdentifier("_cfg") to DepTableValue(
                    DepIdentifier("console") to DepTableValue(
                        DepIdentifier("level") to DepStringValue("TRACE"),
                        DepIdentifier("format") to ConsoleFormat
                    ),
                    DepIdentifier("backend") to DepStringValue("INFO")
                ),
                DepIdentifier("debug") to LogDebug(),
                DepIdentifier("info") to LogInfo()
            )
        )
    }

}


class LogDebug : DepFunctionValue {
    override val identifier = DepIdentifier("debug")
    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
    override fun invoke(ctx: Context): DepValue {
        println("DEBUG: ${ctx.parameters.first().value}")
        return DepNilValue
    }

}


class LogInfo : DepFunctionValue {
    override val identifier = DepIdentifier("info")
    override val metaTable: DepMetaTable get() = TODO("Not yet implemented")
    override fun invoke(ctx: Context): DepValue {
        val first = ctx.parameters.first().value
        if (first is DepIdentifier) {
            println("INFO: ${ctx.env[first]}")
        } else {
            println("INFO: ${first}")
        }

//        println("INFO: ${ctx.parameters.first().value}")
        return DepNilValue
    }

}