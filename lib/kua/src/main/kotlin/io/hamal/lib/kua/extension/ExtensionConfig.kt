package io.hamal.lib.kua.extension

import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableTypeMap
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.Type

class ExtensionConfig(
    val value: MutableMap<String, Type> = mutableMapOf()
)


class ExtensionGetConfigFunction(
    val config: ExtensionConfig
) : Function0In1Out<TableTypeMap>(
    FunctionOutput1Schema(TableTypeMap::class)
) {
    override fun invoke(ctx: FunctionContext): TableTypeMap {
        val result = ctx.state.tableCreateMap(1)
        config.value.forEach { config ->
            val v = config.value
            require(v is StringType) { "Only string config supported for now" }
            result[config.key] = v
        }
        return result
    }
}

class ExtensionUpdateConfigFunction(
    val config: ExtensionConfig
) : Function1In0Out<TableTypeMap>(
    FunctionInput1Schema(TableTypeMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableTypeMap) {
        val state = ctx.state
        state.native.pushNil()
        while (state.native.tableNext(arg1.index)) {
            val k = state.getString(-2)
            val v = state.getAny(-1)

            when (val n = v.value) {
                is StringType -> config.value[k] = n
                else -> TODO()
            }
            state.native.pop(1)
        }
    }
}