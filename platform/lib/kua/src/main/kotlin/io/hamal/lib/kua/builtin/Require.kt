package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.extension.RunnerExtensionRegistry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.StringType

class Require(
    private val registry: RunnerExtensionRegistry
) : Function1In1Out<StringType, TableProxyMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(TableProxyMap::class)
) {
    //FIXME refactor me
    override fun invoke(ctx: FunctionContext, arg1: StringType): TableProxyMap {
        if (registry.isScript(arg1.value)) {
            ctx.setGlobal("_factory", registry.loadScriptExtensionFactory(arg1.value))
            ctx.load("_instance = _factory()")

            val result = ctx.getGlobalTableMap("_instance")

            val config = ctx.tableCreateMap(2)
            config["get"] = registry.scriptExtensions[arg1.value]!!.configGetFunction()
            config["update"] = registry.scriptExtensions[arg1.value]!!.configUpdateFunction()
            result["config"] = config

            ctx.unsetGlobal("_factory")
            ctx.unsetGlobal("_instance")
            ctx.unsetGlobal("extension")

            return result
        }

        if (registry.isPlugin(arg1.value)) {
            ctx.setGlobal("_factory", registry.loadPluginExtensionFactory(arg1.value))
            ctx.load("_instance = _factory()")

            val result = ctx.getGlobalTableMap("_instance")

            val config = ctx.tableCreateMap(2)
            config["get"] = registry.pluginExtensions[arg1.value]!!.configGetFunction()
            config["update"] = registry.pluginExtensions[arg1.value]!!.configUpdateFunction()
            result["config"] = config

            ctx.unsetGlobal("_factory")
            ctx.unsetGlobal("_instance")
            ctx.unsetGlobal("extension")

            return result
        }

        throw IllegalArgumentException("Extension ${arg1.value} not loaded")
    }
}
