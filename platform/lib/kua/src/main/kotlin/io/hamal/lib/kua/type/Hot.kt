package io.hamal.lib.kua.type

import io.hamal.lib.common.hot.*
import io.hamal.lib.common.util.StringUtils
import io.hamal.lib.kua.*
import io.hamal.lib.value.Value
import io.hamal.lib.value.ValueFalse
import io.hamal.lib.value.ValueNil
import io.hamal.lib.value.ValueTrue

//FIXME replace toKua with this
fun HotNode<*>?.toKua(state: State): Value {
    if (this == null) {
        return ValueNil
    }
    return when (this) {
        is HotObject -> state.tableCreate(nodes.map { (key, value) ->
            KuaString(key) to value.toKua(
                state
            )
        }.toMap())

        is HotArray -> state.tableCreate(nodes.map { it.toKua(state) })
        is HotBoolean -> if (value) ValueTrue else ValueFalse
        is HotNull -> ValueNil
        is HotNumber -> KuaNumber(value.toDouble())
        is HotString -> KuaString(value)
        else -> TODO()
    }
}

fun HotNode<*>?.toKuaSnakeCase(state: State): Value {
    if (this == null) {
        return ValueNil
    }
    return when (this) {
        is HotObject -> state.tableCreate(nodes.map { (key, value) ->
            KuaString(StringUtils.snakeCase(key)) to value.toKuaSnakeCase(
                state
            )
        }.toMap())

        is HotArray -> state.tableCreate(nodes.map { it.toKuaSnakeCase(state) })
        is HotBoolean -> if (value) ValueTrue else ValueFalse
        is HotNull -> ValueNil
        is HotNumber -> KuaNumber(value.toDouble())
        is HotString -> KuaString(value)
        else -> TODO()
    }
}


fun Value.toHotNode(): HotNode<*> {
    return when (this) {
        is ValueFalse -> HotBoolean(false)
        is ValueTrue -> HotBoolean(true)
        is KuaCode -> HotString(stringValue)
        is KuaDecimal -> HotString(value.toString())
        is KuaError -> toHotObject()
        is KuaFunction<*, *, *, *> -> TODO()
        is ValueNil -> HotNull
        is KuaNumber -> HotNumber(doubleValue)
        is KuaString -> HotString(stringValue)
        is KuaTable -> {
            if (isArray()) {
                toHotArray()
            } else {
                toHotObject()
            }
        }

        is KuaReference -> TODO()
        else -> TODO()
    }
}

fun KuaError.toHotObject(): HotObject = HotObject.builder().set("message", value).build()

fun KuaTable.isArray(): Boolean {
    return state.checkpoint {
        state.nilPush()
        if (state.tableNext(index).booleanValue) {
            val type = state.type(-2)
            type == KuaNumber::class
        } else {
            false
        }
    }
}

fun KuaTable.toHotArray(): HotArray {
    val builder = HotArray.builder()

    state.checkpoint {
        state.nilPush()
        while (state.tableNext(index).booleanValue) {
            val value = state.get(state.absIndex(-1))

            builder.append(value.toHotNode())

            state.topPop(1)
        }
    }

    return builder.build()
}

fun KuaTable.toHotObject(): HotObject {
    val builder = HotObject.builder()

    state.checkpoint {
        state.nilPush()
        while (state.tableNext(index).booleanValue) {
            val key = state.stringGet(state.absIndex(-2))
            val value = state.get(state.absIndex(-1))

            builder[key.stringValue] = value.toHotNode()

            state.topPop(1)
        }
    }

    return builder.build()
}