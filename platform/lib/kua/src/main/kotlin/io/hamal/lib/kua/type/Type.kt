package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter

sealed interface KuaType {
    enum class Type {
        Any,
        Boolean,
        Code,
        Decimal,
        Error,
        Function,
        Table,
        Nil,
        Number,
        String
    }

    val type: Type

    object Adapter : JsonAdapter<KuaType> {

        override fun serialize(
            src: KuaType, typeOfSrc: java.lang.reflect.Type, context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            element: JsonElement, typeOfT: java.lang.reflect.Type, context: JsonDeserializationContext
        ): KuaType {
            return when (val kuaType = element.asJsonObject.get("type").asString) {
                "Boolean" -> context.deserialize(element, KuaBoolean::class.java)
                "Decimal" -> context.deserialize(element, KuaDecimal::class.java)
                "Error" -> context.deserialize(element, KuaError::class.java)
                "Table" -> context.deserialize(element, KuaTable::class.java)
                "Nil" -> KuaNil
                "Number" -> context.deserialize(element, KuaNumber::class.java)
                "String" -> context.deserialize(element, KuaString::class.java)
                else -> TODO("$kuaType not supported yet")
            }
        }

    }
}

