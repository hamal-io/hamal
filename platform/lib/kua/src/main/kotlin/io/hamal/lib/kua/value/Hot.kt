package io.hamal.lib.kua.value

import io.hamal.lib.common.serialization.json.*
import io.hamal.lib.common.util.StringUtils
import io.hamal.lib.common.value.*
import io.hamal.lib.kua.*

//FIXME replace toKua with this
fun JsonNode<*>?.toKua(state: State): Value {
    if (this == null) {
        return ValueNil
    }
    return when (this) {
        is JsonObject -> state.tableCreate(nodes.map { (key, value) ->
            ValueString(key) to value.toKua(
                state
            )
        }.toMap())

        is JsonArray -> state.tableCreate(nodes.map { it.toKua(state) })
        is JsonBoolean -> if (value) ValueTrue else ValueFalse
        is JsonNull -> ValueNil
        is JsonNumber -> ValueNumber(value.toDouble())
        is JsonString -> ValueString(value)
        else -> TODO()
    }
}

fun Value?.toKua(state: State): Value {
    if (this == null) {
        return ValueNil
    }
//    return when (this) {
//        is HotObject -> state.tableCreate(nodes.map { (key, value) ->
//            ValueString(key) to value.toKua(
//                state
//            )
//        }.toMap())
//
//        is HotArray -> state.tableCreate(nodes.map { it.toKua(state) })
//        is HotBoolean -> if (value) ValueTrue else ValueFalse
//        is HotNull -> ValueNil
//        is HotNumber -> ValueNumber(value.toDouble())
//        is HotString -> ValueString(value)
//        else -> TODO()
//    }
    return when (this) {
        is ValueObject -> state.tableCreate(properties.associate { (key, value) ->
            ValueString(key.stringValue) to value.toKua(
                state
            )
        })

        is ValueArray -> state.tableCreate(value.map { it.toKua(state) })
        else -> this
    }
}


fun JsonNode<*>?.toKuaSnakeCase(state: State): Value {
    if (this == null) {
        return ValueNil
    }
    return when (this) {
        is JsonObject -> state.tableCreate(nodes.map { (key, value) ->
            ValueString(StringUtils.snakeCase(key)) to value.toKuaSnakeCase(
                state
            )
        }.toMap())

        is JsonArray -> state.tableCreate(nodes.map { it.toKuaSnakeCase(state) })
        is JsonBoolean -> if (value) ValueTrue else ValueFalse
        is JsonNull -> ValueNil
        is JsonNumber -> ValueNumber(value.toDouble())
        is JsonString -> ValueString(value)
        else -> TODO()
    }
}


fun Value?.toKuaSnakeCase(state: State): Value {
    if (this == null) {
        return ValueNil
    }

    return when (this) {
        is ValueObject -> state.tableCreate(properties.associate { (key, value) ->
            ValueString(StringUtils.snakeCase(key.stringValue)) to value.toKuaSnakeCase(
                state
            )
        })

        is ValueArray -> state.tableCreate(value.map { it.toKuaSnakeCase(state) })
        else -> this
    }
}


fun Value.toHotNode(): JsonNode<*> {
    return when (this) {
        is ValueBoolean -> JsonBoolean(false)
        is ValueCode -> JsonString(stringValue)
        is ValueDecimal -> JsonString(value.toString())
        is ValueError -> toHotObject()
        is KuaFunction<*, *, *, *> -> TODO()
        is ValueNil -> JsonNull
        is ValueNumber -> JsonNumber(doubleValue)
        is ValueString -> JsonString(stringValue)
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

fun ValueError.toHotObject(): JsonObject = JsonObject.builder().set("message", stringValue).build()

fun KuaTable.isArray(): Boolean {
    return state.checkpoint {
        state.nilPush()
        if (state.tableNext(index).booleanValue) {
            val type = state.type(-2)
            type == ValueNumber::class
        } else {
            false
        }
    }
}

fun KuaTable.toHotArray(): JsonArray {
    val builder = JsonArray.builder()

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

fun KuaTable.toHotObject(): JsonObject {
    val builder = JsonObject.builder()

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

fun KuaTable.toValueObject(): ValueObject {
    val builder = ValueObject.builder()

    state.checkpoint {
        state.nilPush()
        while (state.tableNext(index).booleanValue) {
            val key = state.stringGet(state.absIndex(-2))
            val value = state.get(state.absIndex(-1))

            builder[key.stringValue] = value

            state.topPop(1)
        }
    }

    return builder.build()
}