package io.hamal.lib.kua.extension

import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value

class ExtensionConfig(
    val value: MutableMap<String, Value> = mutableMapOf()
)


class ExtensionGetConfigFunction(
    val config: ExtensionConfig
) : Function0In1Out<TableMapValue>(
    FunctionOutput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext): TableMapValue {
        val result = ctx.state.tableCreateMap(1)
        config.value.forEach { config ->
            val v = config.value
            require(v is StringValue) { "Only string config supported for now" }
            result[config.key] = v
        }
        return result
    }
}

class ExtensionUpdateConfigFunction(
    val config: ExtensionConfig
) : Function1In0Out<TableMapValue>(
    FunctionInput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue) {
        val state = ctx.state
        state.native.pushNil()
        while (state.native.tableNext(arg1.index)) {
            val k = state.getString(-2)
            val v = state.getAnyValue(-1)

            when (val n = v.value) {
                is StringValue -> config.value[k] = n
                else -> TODO()
            }
            state.native.pop(1)
        }
    }
}