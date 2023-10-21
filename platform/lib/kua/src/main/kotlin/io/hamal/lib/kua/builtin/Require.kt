package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.plugin.PluginRegistry
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.StringType

class Require(
    private val registry: PluginRegistry
) : Function1In1Out<StringType, TableProxyMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): TableProxyMap {
        // FIXME support extension
        ctx.setGlobal("_factory", registry.loadCapabilityFactory(arg1.value))
        ctx.load("_instance = _factory()")

        val result = ctx.getGlobalTableMap("_instance")

        val config = ctx.tableCreateMap(2)
        config["get"] = registry.capabilities[arg1.value]!!.getConfigFunction()
        config["update"] = registry.capabilities[arg1.value]!!.updateConfigFunction()
        result["config"] = config

        ctx.unsetGlobal("_factory")
        ctx.unsetGlobal("_instance")
        ctx.unsetGlobal("extension")
        ctx.unsetGlobal("createExtension")

        return result
    }
}
