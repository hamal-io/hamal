package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.extend.RunnerRegistry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.StringType

class RequirePlugin(
    private val registry: RunnerRegistry
) : Function1In1Out<StringType, TableProxyMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): TableProxyMap {
        ctx.setGlobal("_factory", registry.loadPluginFactory(arg1.value))
        ctx.load("_instance = _factory()")

        val result = ctx.getGlobalTableMap("_instance")

        val config = ctx.tableCreateMap(2)
        config["get"] = registry.plugins[arg1.value]!!.configGetFunction()
        config["update"] = registry.plugins[arg1.value]!!.configUpdateFunction()
        result["config"] = config

        ctx.unsetGlobal("_factory")
        ctx.unsetGlobal("_instance")
        ctx.unsetGlobal("extension")

        return result
    }
}
