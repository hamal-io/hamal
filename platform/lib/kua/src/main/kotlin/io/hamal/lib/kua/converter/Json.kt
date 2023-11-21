package io.hamal.lib.kua.converter

import io.hamal.lib.kua.type.*
import kotlinx.serialization.json.*

fun SerializableType.toJson(): JsonElement {
    return when (this) {
        is AnySerializableType -> value.toJson()
        is False -> JsonPrimitive(false)
        is True -> JsonPrimitive(true)
        is CodeType -> JsonPrimitive(value)
        is DecimalType -> JsonPrimitive(toString())
        is ErrorType -> JsonPrimitive(value)
        is NilType -> JsonNull
        is NumberType -> JsonPrimitive(value)
        is StringType -> JsonPrimitive(value)
        is ArrayType -> toJson()
        is MapType -> toJson()
    }
}

private fun MapType.toJson(): JsonObject {
    return JsonObject(this.value.map { (key, value) ->
        key to value.toJson()
    }.toMap())
}

private fun ArrayType.toJson(): JsonArray {
    return JsonArray(this.value.map { (_, value) -> value.toJson() }.toList())
}

fun JsonElement.convertToType(): SerializableType {
    return when (this) {
        is JsonNull -> NilType
        is JsonPrimitive -> convertToType()
        is JsonObject -> convertToType()
        is JsonArray -> convertToType()
    }
}

fun JsonArray.convertToType(): ArrayType {
    val arr = this
    return ArrayType(
        arr.mapIndexed { index, item -> (index + 1) to item.convertToType() }.toMap().toMutableMap()
    )
}

fun JsonObject.convertToType(): MapType {
    val arr = this
    return MapType(
        arr.map { (key, item) -> key to item.convertToType() }.toMap().toMutableMap()
    )
}

fun JsonPrimitive.convertToType(): SerializableType {
    if (isString) {
        return StringType(content)
    }

    if (content == "true") {
        return True
    }

    if (content == "false") {
        return False
    }

    return NumberType(content.toDouble())
}