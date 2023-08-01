package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.Registry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue

class Require(
    val registry: Registry
) : Function1In1Out<StringValue, TableMapValue>(
    FunctionInput1Schema(StringValue::class),
    FunctionOutput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue): TableMapValue {
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
