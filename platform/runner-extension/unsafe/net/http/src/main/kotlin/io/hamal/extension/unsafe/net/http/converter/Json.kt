package io.hamal.extension.unsafe.net.http.converter

import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.DecimalType.Companion.isNumber
import kotlinx.serialization.json.*

fun JsonElement.convertToType(): SerializableType {
    return when (this) {
        is JsonNull -> NilType
        is JsonPrimitive -> convertToType()
        is JsonObject -> convertToType()
        is JsonArray -> convertToType()
    }
}

private fun JsonArray.convertToType(): ArrayType {
    val arr = this
    return ArrayType(
        arr.mapIndexed { index, item ->
            (index + 1) to item.convertToType()
        }.toMap().toMutableMap()
    )
}

private fun JsonObject.convertToType(): MapType {
    val arr = this
    return MapType(
        arr.map { (key, item) ->
            key to item.convertToType()
        }.toMap().toMutableMap()
    )
}

private fun JsonPrimitive.convertToType(): SerializableType {
    if (isString) {
        if (isNumber(content)) {
            return DecimalType(content)
        }
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