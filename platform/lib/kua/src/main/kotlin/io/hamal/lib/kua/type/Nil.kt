package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.kua.type.KuaType.Type.Nil
import java.lang.reflect.Type


object KuaNil : KuaType {
    override val type: KuaType.Type = Nil

    object Adapter : JsonAdapter<KuaNil> {
        override fun serialize(instance: KuaNil, type: Type, ctx: JsonSerializationContext): JsonElement {
            return ctx.serialize(
                HotObject.builder()
                    .set("type", instance.type.name)
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): KuaNil {
            return KuaNil
        }
    }
}