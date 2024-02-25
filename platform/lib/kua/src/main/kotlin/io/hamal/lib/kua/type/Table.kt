package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.kua.State

interface KuaTable : KuaType {
    val index: Int
    val state: State

    object Adapter : JsonAdapter<KuaTable> {
        override fun serialize(
            instance: KuaTable, type: java.lang.reflect.Type, ctx: JsonSerializationContext
        ): JsonElement {
            TODO()
        }

        override fun deserialize(
            element: JsonElement,
            type: java.lang.reflect.Type,
            ctx: JsonDeserializationContext
        ): KuaTable {
            TODO()
        }
    }
}

