package io.hamal.lib.common.value.serde

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterGeneric
import io.hamal.lib.common.value.ValueType
import java.lang.reflect.Type

internal data object ValueTypeAdapter : AdapterGeneric<ValueType> {
    override fun serialize(
        src: ValueType,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.value)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ValueType {
        return ValueType(json.asString)
    }
}