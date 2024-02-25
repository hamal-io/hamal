package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter

data class KuaAny(val value: KuaType) : KuaType {
    override val type: KuaType.Type = KuaType.Type.Any

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