package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.serialization.GsonSerde
import io.hamal.lib.kua.type.KuaType.Type

fun booleanOf(value: Boolean) = if (value) KuaTrue else KuaFalse

sealed class KuaBoolean(
    val value: Boolean,
) : KuaType {
    override val type: Type = Type.Boolean

    object Serde : GsonSerde<KuaBoolean> {
        override fun serialize(
            instance: KuaBoolean,
            type: java.lang.reflect.Type,
            ctx: JsonSerializationContext
        ): JsonElement {
            return ctx.serialize(
                HotObject.builder()
                    .set("value", instance.value)
                    .set("type", instance.type.name)
                    .build()
            )
        }

        override fun deserialize(
            element: JsonElement,
            type: java.lang.reflect.Type,
            ctx: JsonDeserializationContext
        ): KuaBoolean {
            return when (val kuaType = element.asJsonObject.get("value").asString.lowercase()) {
                "true" -> KuaTrue
                "false" -> KuaFalse
                else -> throw IllegalArgumentException("Expected True or False, but got $kuaType")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KuaBoolean
        if (value != other.value) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

object KuaTrue : KuaBoolean(true) {
    override fun toString() = "true"

    object Serde : GsonSerde<KuaTrue> {
        override fun serialize(
            instance: KuaTrue,
            type: java.lang.reflect.Type,
            ctx: JsonSerializationContext
        ): JsonElement {
            return ctx.serialize(
                HotObject.builder()
                    .set("value", instance.value)
                    .set("type", instance.type.name)
                    .build()
            )
        }

        override fun deserialize(
            element: JsonElement,
            type: java.lang.reflect.Type,
            ctx: JsonDeserializationContext
        ): KuaTrue {
            return KuaTrue
        }
    }
}

object KuaFalse : KuaBoolean(false) {
    override fun toString() = "false"
}