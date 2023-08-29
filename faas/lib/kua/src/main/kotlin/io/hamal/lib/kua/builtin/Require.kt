package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.extension.ExtensionRegistry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.StringType

class Require(
    val registry: ExtensionRegistry
) : Function1In1Out<StringType, TableProxyMap>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput1Schema(TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): TableProxyMap {
        ctx.setGlobal("_factory", registry.loadFactory(arg1.value))
        ctx.load(
            """
            _instance = _factory()
        """.trimMargin()
        )

        val result = ctx.getGlobalTableMap("_instance")

        val config = ctx.tableCreateMap(2)
        config["get"] = registry.extensions[arg1.value]!!.getConfigFunction()
        config["update"] = registry.extensions[arg1.value]!!.updateConfigFunction()
        result["config"] = config

        ctx.unsetGlobal("_factory")
        ctx.unsetGlobal("_instance")
        ctx.unsetGlobal("extension")
        ctx.unsetGlobal("createExtension")

        return result
    }
}
