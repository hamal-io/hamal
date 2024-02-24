package io.hamal.lib.kua.converter

import com.google.gson.*
import io.hamal.lib.kua.type.*


fun KuaType.toJson(): JsonElement {
    return when (this) {
        is KuaAny -> value.toJson()
        is KuaFalse -> JsonPrimitive(false)
        is KuaTrue -> JsonPrimitive(true)
        is KuaCode -> JsonPrimitive(value)
        is KuaDecimal -> JsonPrimitive(toString())
        is KuaError -> JsonPrimitive(value)
        is KuaNil -> JsonNull.INSTANCE
        is KuaNumber -> JsonPrimitive(value)
        is KuaString -> JsonPrimitive(value)
        is KuaTable -> toJson()
        is KuaFunction<*, *, *, *> -> TODO()
        is KuaTableType -> TODO()
    }
}

// FIXME JSON NODE
private fun KuaTable.toJson(): JsonObject {
    val result = JsonObject()

    this.value.forEach { (key, value) ->
        result.add(key, value.toJson())
    }

    return result
}

//private fun KuaTable.toJson(): JsonArray {
//    val result = JsonArray()
//
//    this.value.forEach { (_, value) ->
//        result.add(value.toJson())
//    }
//
//    return result
//}

fun JsonElement.convertToType(): KuaType {
    return when (this) {
        is JsonNull -> KuaNil
        is JsonPrimitive -> convertToType()
        is JsonObject -> convertToType()
        is JsonArray -> convertToType()
        else -> TODO()
    }
}

// FIXME JSON NODE
//fun JsonArray.convertToType(): KuaArray {
//    val arr = this
//    return KuaArray(
//        arr.mapIndexed { index, item -> (index + 1) to item.convertToType() }.toMap().toMutableMap()
//    )
//}

fun JsonObject.convertToType(): KuaTable {
    val obj = this

    return KuaTable(
        obj.entrySet().associate { (key, item) -> key to item.convertToType() }.toMutableMap()
    )
}

fun JsonPrimitive.convertToType(): KuaType {
    if (isString) {
        return KuaString(asString)
    }

    if (asString == "true") {
        return KuaTrue
    }

    if (asString == "false") {
        return KuaFalse
    }

    return KuaNumber(asDouble)
}