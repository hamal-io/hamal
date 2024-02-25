package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.kua.type.KuaType.Type

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
                "Table" -> context.deserialize(element, KuaTableType::class.java)
                "Nil" -> KuaNil
                "Number" -> context.deserialize(element, KuaNumber::class.java)
                "String" -> context.deserialize(element, KuaString::class.java)
                else -> TODO("$kuaType not supported yet")
            }
        }

    }
}

interface KuaTableType : KuaType {
    object Adapter : JsonAdapter<KuaTableType> {
        override fun serialize(
            instance: KuaTableType, type: java.lang.reflect.Type, ctx: JsonSerializationContext
        ): JsonElement {
            TODO()
        }

        override fun deserialize(
            element: JsonElement,
            type: java.lang.reflect.Type,
            ctx: JsonDeserializationContext
        ): KuaTableType {
            TODO()
        }
    }
}

data class KuaAny(val value: KuaType) : KuaType {
    override val type: Type = Type.Any

    object Adapter : JsonAdapter<KuaAny> {
        override fun serialize(
            instance: KuaAny, type: java.lang.reflect.Type, ctx: JsonSerializationContext
        ): JsonElement {
            return ctx.serialize(instance.value)
        }

        override fun deserialize(
            element: JsonElement, type: java.lang.reflect.Type, ctx: JsonDeserializationContext
        ): KuaAny {
            return KuaAny(KuaType.Adapter.deserialize(element, KuaType::class.java, ctx))
        }
    }
}