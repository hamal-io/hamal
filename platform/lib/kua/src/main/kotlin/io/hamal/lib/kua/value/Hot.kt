package io.hamal.lib.kua.value

import io.hamal.lib.common.serialization.serde.*
import io.hamal.lib.common.serialization.serde.SerdeNumber
import io.hamal.lib.common.serialization.serde.SerdeString
import io.hamal.lib.common.util.StringUtils
import io.hamal.lib.common.value.*
import io.hamal.lib.kua.*

//FIXME replace toKua with this
fun SerdeNode<*>?.toKua(state: State): Value {
    if (this == null) {
        return ValueNil
    }
    return when (this) {
        is SerdeObject -> state.tableCreate(nodes.map { (key, value) ->
            ValueString(key) to value.toKua(
                state
            )
        }.toMap())

        is SerdeArray -> state.tableCreate(nodes.map { it.toKua(state) })
        is io.hamal.lib.common.serialization.serde.SerdeBoolean -> if (value) ValueTrue else ValueFalse
        is SerdeNull -> ValueNil
        is SerdeNumber -> ValueNumber(value.toDouble())
        is SerdeString -> ValueString(value)
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
    TODO()
}


fun SerdeNode<*>?.toKuaSnakeCase(state: State): Value {
    if (this == null) {
        return ValueNil
    }
    return when (this) {
        is SerdeObject -> state.tableCreate(nodes.map { (key, value) ->
            ValueString(StringUtils.snakeCase(key)) to value.toKuaSnakeCase(
                state
            )
        }.toMap())

        is SerdeArray -> state.tableCreate(nodes.map { it.toKuaSnakeCase(state) })
        is io.hamal.lib.common.serialization.serde.SerdeBoolean -> if (value) ValueTrue else ValueFalse
        is SerdeNull -> ValueNil
        is SerdeNumber -> ValueNumber(value.toDouble())
        is SerdeString -> ValueString(value)
        else -> TODO()
    }
}


fun Value?.toKuaSnakeCase(state: State): Value {
    if (this == null) {
        return ValueNil
    }
//    return when (this) {
//        is ValueObject -> state.tableCreate(nodes.map { (key, value) ->
//            ValueString(StringUtils.snakeCase(key)) to value.toKuaSnakeCase(
//                state
//            )
//        }.toMap())
//
//        is ValueArray -> state.tableCreate(nodes.map { it.toKuaSnakeCase(state) })
////        is ValueBoolean -> if (value) ValueTrue else ValueFalse
////        is ValueNil -> ValueNil
////        is ValueNumber -> ValueNumber(value.toDouble())
////        is ValueString -> ValueString(value)
//        else -> TODO()
//    }
    TODO()
}


fun Value.toHotNode(): SerdeNode<*> {
    return when (this) {
        is ValueFalse -> SerdeBoolean(false)
        is ValueTrue -> SerdeBoolean(true)
        is ValueCode -> SerdeString(stringValue)
        is ValueDecimal -> SerdeString(value.toString())
        is ValueError -> toHotObject()
        is KuaFunction<*, *, *, *> -> TODO()
        is ValueNil -> SerdeNull
        is ValueNumber -> SerdeNumber(doubleValue)
        is ValueString -> SerdeString(stringValue)
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

fun ValueError.toHotObject(): SerdeObject = SerdeObject.builder().set("message", stringValue).build()

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

fun KuaTable.toHotArray(): SerdeArray {
    val builder = SerdeArray.builder()

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

fun KuaTable.toHotObject(): SerdeObject {
    val builder = SerdeObject.builder()

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