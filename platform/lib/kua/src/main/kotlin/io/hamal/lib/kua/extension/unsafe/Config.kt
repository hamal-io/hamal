package io.hamal.lib.kua.extension.unsafe

import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.Type

class RunnerUnsafeExtensionConfig(
    val value: MutableMap<String, Type> = mutableMapOf()
)

class RunnerUnsafeExtensionGetConfigFunction(
    val config: RunnerUnsafeExtensionConfig
) : Function0In1Out<TableProxyMap>(
    FunctionOutput1Schema(TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext): TableProxyMap {
        val result = ctx.state.tableCreateMap(1)
        config.value.forEach { config ->
            val v = config.value
            require(v is StringType) { "Only string config supported for now" }
            result[config.key] = v
        }
        return result
    }
}

class RunnerUnsafeExtensionUpdateConfigFunction(
    val config: RunnerUnsafeExtensionConfig
) : Function1In0Out<TableProxyMap>(
    FunctionInput1Schema(TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxyMap) {
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