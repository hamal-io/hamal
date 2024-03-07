package io.hamal.lib.kua.type

import io.hamal.lib.common.hot.*
import io.hamal.lib.kua.*

//FIXME replace toKua with this
fun HotNode.toKua(state: State): KuaType {
    return when (this) {
        is HotObject -> state.tableCreate(nodes.map { (key, value) -> KuaString(key) to value.toKua(state) }.toMap())
        is HotArray -> state.tableCreate(nodes.map { it.toKua(state) })
        is HotBoolean -> if (value) KuaTrue else KuaFalse
        is HotNull -> KuaNil
        is HotNumber -> KuaNumber(value.toDouble())
        is HotString -> KuaString(value)
        else -> TODO()
    }
}


fun KuaType.toHotNode(): HotNode {
    return when (this) {
        is KuaFalse -> HotBoolean(false)
        is KuaTrue -> HotBoolean(true)
        is KuaCode -> HotString(stringValue)
        is KuaDecimal -> HotString(value.toString())
        is KuaError -> toHotObject()
        is KuaFunction<*, *, *, *> -> TODO()
        is KuaNil -> HotNull
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